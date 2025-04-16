package com.husttwj.imagecompress.util


import java.io.File
import java.nio.charset.Charset

class WindowsHelper : OSHelper() {

    override fun init() {
    }

    override val userName: String
        get() {
            try {
                var userName = ""
                var result = execCommand("git config --global user.name")
                if (result.resultCode == 0) {
                    userName = result.resultMsg!!.trim()
                }
                if (userName.isEmpty()) {
                    result = execCommand("git config --global user.email")
                    if (result.resultCode == 0) {
                        userName = result.resultMsg!!.trim()
                        val indexOfAt = userName.indexOf("@")
                        if (indexOfAt > -1) {
                            userName = userName.substring(0, indexOfAt).trim()
                        }
                    }
                }
                if (userName.isEmpty()) {
                    userName = System.getenv()["USERNAME"]?.trim() ?: ""
                }
                if (userName.isEmpty()) {
                    userName = uid
                }
                return userName
            } catch (t: Throwable) {
            }
            return uid
        }

    override val uid: String
        get() {
            try {
                val result = execCommand("whoami /user")
                if (result.resultCode == 0) {
                    val split = result.resultMsg!!.split("\n")
                    for (i in (split.size - 1) downTo 0) {
                        if (split[i].trim().isEmpty()) {
                            continue
                        }
                        val sidLines = split[i].split(" ")
                        return sidLines[sidLines.size - 1]
                    }
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
            return ""
        }

    override val aaptFilePath: String
        get() {
            val sdkFile = getAndroidSdkFile() ?: return ""
            val file = File(sdkFile, "build-tools")
            if (file.exists()) {
                val aaptFile = FileUtils.findFileByName(file, "aapt.exe", 3) { o1, o2 -> o2.name.compareTo(o1.name) }
                if (aaptFile != null) {
                    return aaptFile.absolutePath
                }
            }
            return ""
        }

    override fun getGitUrlByPath(filePath: String): String {
        try {
            val execResult = execCommand("cd \"${filePath}\" & git remote -v")
            if (execResult.resultCode == 0) {
                val gitline = execResult.resultMsg!!
                val startGit = gitline.indexOf("git")
                if (startGit >= 0) {
                    val endIndex = gitline.indexOf(".git")
                    if (endIndex > -1) {
                        return gitline.substring(startGit, endIndex + ".git".length)
                    }
                }
            }
        } catch (t: Throwable) {
            LogUtil.e("getGitUrlByPath", t)
        }
        return filePath
    }

    override fun getApkPkgName(apkFilePath: String?): String? {
        apkFilePath ?: return null
        if (aaptFilePath.isEmpty()) {
            return null
        }
        try {
            val execCommand =
                execCommand(
                    false, true,
                    "\"${
                    aaptFilePath.replace(
                        " ",
                        "` "
                    )
                    }\" dump badging \"${apkFilePath.replace(" ", "` ")}\""
                )
            val result = execCommand.resultMsg!!
            val grepLine = StringUtils.grepLine(result, "package") ?: return null
            val indexOfStart = grepLine.indexOf("name='")
            if (indexOfStart > -1) {
                val indexOfEnd = grepLine.indexOf("'", indexOfStart + "name='".length)
                if (indexOfEnd > -1) {
                    return grepLine.substring(indexOfStart + "name='".length, indexOfEnd)
                }
            }
        } catch (t: Throwable) {
            LogUtil.e("getApkName Error", t)
        }
        return null
    }

    @Throws(java.lang.Exception::class)
    override fun execCommand(vararg command: String): ExecResult {
        return execCommand(false, false, *command)
    }

    @Throws(java.lang.Exception::class)
    fun execCommand(nohub: Boolean, powerShell: Boolean, vararg commands: String): ExecResult {
        for (i in commands.indices) {
            var shellCmds = if (powerShell) {
                arrayOf("cmd", "/C", "powershell", commands[i].trim())
            } else if (nohub) {
                arrayOf("cmd", "/C", "start /min \"n\" ${commands[i].trim()}")
            } else {
                arrayOf("cmd", "/C", commands[i].trim())
            }
            val exec = Runtime.getRuntime().exec(shellCmds)
            var byteArrayOutputStream = FileUtils.readByteArrayOutputStream(exec.inputStream)
            exec.waitFor()
            val resuleCode = exec.exitValue()
            val errorResultStream = FileUtils.readByteArrayOutputStream(exec.errorStream)
            if (byteArrayOutputStream.size() == 0 || (resuleCode != 0 && errorResultStream.size() > 0)) {
                byteArrayOutputStream = errorResultStream;
            }
            if (i == commands.size - 1) {
                return ExecResult(
                    resuleCode,
                    if (resuleCode == 0) String(byteArrayOutputStream.toByteArray(), Charset.forName("GB2312")) else null,
                    if (resuleCode != 0) String(byteArrayOutputStream.toByteArray(), Charset.forName("GB2312")) else null
                )
            }
        }
        return ExecResult(0, null, null)
    }

    private fun getRootPath(path: String) : String {
        val indexOfSeparator = path.indexOf(File.separator)
        if (indexOfSeparator > -1) {
            return path.substring(0, indexOfSeparator)
        }
        return ""
    }

}