package com.husttwj.imagecompress.util


object StringUtils {

    fun getFileSize(fileSize: Long): String {
        return getFileSize(fileSize, false)
    }

    @JvmStatic
    fun getFileSize(fileSize: Long, withOriginSize: Boolean): String {
        var unit = "B"
        var size: Float
        if (fileSize < 1000) {
            size = fileSize.toFloat()
        } else if (fileSize < 1000L * 1000L) {
            unit = "KB"
            size = (1.0f * fileSize / (1000L))
        } else if (fileSize < 1000L * 1000L * 1000L) {
            unit = "M"
            size = (1.0f * fileSize / (1000L * 1000L))
        } else if (fileSize < 1000L * 1000L * 1000L * 1000L) {
            unit = "G"
            size = (1.0f * fileSize / (1000L * 1000L * 1000L))
        } else {
            unit = "T"
            size = (1.0f * fileSize / (1000L * 1000L * 1000L * 1000L))
        }
        return String.format("%.1f", size) + unit + (if (withOriginSize) (" (" + fileSize + "B)") else "")
    }
}
