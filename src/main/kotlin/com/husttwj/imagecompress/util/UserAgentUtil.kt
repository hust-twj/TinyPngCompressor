package com.husttwj.imagecompress.util

object UserAgentUtil {

    private const val DEFAULT_UA =
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 14_6_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36"

    private val chromeVersions = listOf(
        "119.0.6045.105",
        "118.0.5993.88",
        "117.0.5938.92",
        "114.0.5735.133",
        "111.0.5563.64",
        "98.0.4758.80"
    )

    fun getUserAgent(): String {
        val osName = System.getProperty("os.name").lowercase()
        val osVersion = System.getProperty("os.version") ?: ""
        val arch = System.getProperty("os.arch") ?: ""

        val platform = when {
            osName.contains("mac") -> "Macintosh; Intel Mac OS X ${formatMacVersion(osVersion)}"
            osName.contains("win") -> "Windows NT ${formatWindowsVersion(osVersion)}; $arch"
            osName.contains("nux") || osName.contains("nix") -> "X11; Linux $arch"
            else -> ""
        }

        if (platform.isEmpty()) {
            return DEFAULT_UA
        }
        val chrome = "Chrome/${chromeVersions.random()}"
        val safari = "Safari/537.36"

        return "Mozilla/5.0 ($platform) AppleWebKit/537.36 (KHTML, like Gecko) $chrome $safari"
    }

    private fun formatMacVersion(version: String): String {
        return version.replace(".", "_")
    }

    private fun formatWindowsVersion(version: String): String {
        return version
    }
}