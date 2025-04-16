package com.husttwj.imagecompress.util


import java.io.File
import java.nio.charset.Charset

class MacHelper : OSHelper() {

    private var sPluginShellPath: String? = null

    override fun init() {
        sPluginShellPath =
            FileUtils.sPluginDir.replace(" ", "\\ ")
        if (!File(FileUtils.sPluginDir, "imgcopy").exists()) {
            try {
                execCommand("gcc -Wall -g -O3 -ObjC -framework Foundation -framework AppKit -o $sPluginShellPath/imgcopy $sPluginShellPath/imgcopy.m")
            } catch (ignore: Exception) {
            }
        }
        try {
            execCommand("chmod a+x $sPluginShellPath/imgcopy")
        } catch (e: Exception) {
            LogUtil.e("组件初始化失败 Path: $sPluginShellPath", e)
        }
    }

    override val userName: String
        get() {
            var name = ""
            try {
                var result = execCommand("git config --global user.email")
                if (result.resultCode == 0) {
                    name = result.resultMsg!!.trim()
                    if (name.contains("@")) {
                        name = name.substring(0, name.indexOf("@"))
                    }
                }
                if (name.isEmpty()) {
                    result = execCommand("git config user.email")
                    if (result.resultCode == 0) {
                        name = result.resultMsg!!.trim()
                        if (name.contains("@")) {
                            name = name.substring(0, name.indexOf("@"))
                        }
                    }
                }
                if (name.isEmpty()) {
                    result = execCommand("git config --global user.name")
                    if (result.resultCode == 0) {
                        name = result.resultMsg!!.trim()
                    }
                }
                if (name.isEmpty()) {
                    val userHomePath = System.getProperty("user.home")
                    val file = File(userHomePath, ".ssh/id_rsa.pub")
                    if (file.exists()) {
                        val fileContent = FileUtils.getFileContent(file)
                        if (fileContent != null) {
                            val contents = fileContent.trim { it <= ' ' }.split(" ")
                            if (contents.size >= 3) {
                                val email = contents[2]
                                if (email.contains("@")) {
                                    name = email.substring(0, email.indexOf("@"))
                                } else {
                                    name = email
                                }
                            }
                        }
                    }
                }
                if (name.isEmpty()) {
                    name = System.getProperty("user.name")?.trim() ?: ""
                }
            } catch (t: Throwable) {
            }
            return name
        }

    override val uid: String
        get() {
            try {
                val execResult =
                    execCommand("ioreg -rd1 -w0 -c IONVMeBlockStorageDevice| grep 'Serial' | awk -F '=' '{print $3}' | awk -F ',' '{print $1}' | sed 's/\"//g'")
                if (execResult.resultCode == 0) {
                    return execResult.resultMsg!!.trim { it <= ' ' }
                }
            } catch (t: Throwable) {
            }
            return ""
        }


    override val aaptFilePath: String
        get() {
            val sdkFile = getAndroidSdkFile() ?: return ""
            val file = File(sdkFile, "build-tools")
            if (file.exists()) {
                val aaptFile = FileUtils.findFileByName(file, "aapt", 3) { o1, o2 -> o2.name.compareTo(o1.name) }
                if (aaptFile != null) {
                    return aaptFile.absolutePath
                }
            }
            return ""
        }

    override fun getApkPkgName(apkFilePath: String?): String? {
        apkFilePath ?: return null
        if (aaptFilePath.isEmpty()) {
            return null
        }
        try {
            val execCommand = execCommand(
                "${aaptFilePath.replace(
                    " ",
                    "\\ "
                )} dump badging '$apkFilePath' | grep package"
            )
            val line = execCommand.resultMsg ?: ""
            val indexOfStart = line.indexOf("name='")
            if (indexOfStart > -1) {
                val indexOfEnd = line.indexOf("'", indexOfStart + "name='".length)
                if (indexOfEnd > -1) {
                    return line.substring(indexOfStart + "name='".length, indexOfEnd)
                }
            }
        } catch (t: Throwable) {
            LogUtil.e("getApkName Error", t)
        }
        return null
    }

    override fun getGitUrlByPath(filePath: String): String {
        try {
            val execResult: ExecResult =
                execCommand("cd '$filePath'; git remote -v | grep fetch | head -1")
            if (execResult.resultCode == 0) {
                val gitline = execResult.resultMsg!!
                val startGit = gitline.indexOf("git")
                if (startGit >= 0) {
                    val endIndex = gitline.lastIndexOf(".git")
                    if (endIndex > -1) {
                        return gitline.substring(startGit, endIndex + ".git".length)
                    }
                }
            }
        } catch (t: Throwable) {
            LogUtil.e("getGitUrlByPath error", t)
        }
        return filePath
    }

    @Throws(java.lang.Exception::class)
    override fun execCommand(vararg command: String): ExecResult {
        return execCommand(false, *command)
    }

    @Throws(java.lang.Exception::class)
    fun execCommand(noHup: Boolean, vararg commands: String): ExecResult {
        for (i in commands.indices) {
            var zshCommands = if (noHup) {
                arrayOf("/bin/zsh", "-c", commands[i])
            } else {
                arrayOf("nohup", "/bin/zsh", "-c", commands[i])
            }
            val exec = Runtime.getRuntime().exec(zshCommands)
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
                    if (resuleCode == 0) String(byteArrayOutputStream.toByteArray(), Charset.forName("UTF-8")) else null,
                    if (resuleCode != 0) String(byteArrayOutputStream.toByteArray(), Charset.forName("UTF-8")) else null
                )
            }
        }
        return ExecResult(0, null, null)
    }

}