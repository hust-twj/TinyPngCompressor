package com.husttwj.imagecompress.service

import com.husttwj.imagecompress.PluginGlobalSettings
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.tinify.Tinify
import java.io.IOException


class TinyPNG {

    val isImageProcessed: Boolean = false

    val compressionCount: Int
        get() = Tinify.compressionCount()

    companion object {
        @Throws(IOException::class)
        fun process(file: VirtualFile): ByteArray {
            if (StringUtil.isEmptyOrSpaces(Tinify.key())) {
                val settings = PluginGlobalSettings.getInstance()
                Tinify.setKey(settings.apiKey)
            }

            return Tinify.fromFile(file.path).toBuffer()
        }
    }
}

