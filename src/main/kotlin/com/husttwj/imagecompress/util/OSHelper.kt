package com.husttwj.imagecompress.util

import javax.swing.filechooser.FileSystemView

abstract class OSHelper {

    companion object {

        private val sIsWindows = System.getProperty("os.name").toLowerCase().indexOf("windows") > -1

        @JvmStatic
        val instance: OSHelper = if (sIsWindows) WindowsHelper() else MacHelper()

    }

    abstract fun init()

    abstract val uid: String

    abstract val userName: String

    abstract fun getGitUrlByPath(filePath: String): String

    open fun getUserDesktopFilePath(): String = FileSystemView.getFileSystemView().homeDirectory.absolutePath


    abstract fun execCommand(vararg command: String): ExecResult

}

class ExecResult(val resultCode: Int, val resultMsg: String?, val errorMsg: String?) {

    override fun toString(): String {
        return "ExecResult{" +
            "resultCode=" + resultCode +
            ", resultMsg=" + resultMsg +
            ", errorMsg=" + errorMsg + '}'
    }

}