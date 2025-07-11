package com.husttwj.imagecompress.util

import org.apache.commons.codec.digest.DigestUtils
import java.io.File
import java.io.FileInputStream

object MD5Utils {

    fun getMD5(file: File): String {
        try {
            return DigestUtils.md5Hex(FileInputStream(file))
        } catch (e: Exception) {
        }
        return ""
    }

    @JvmStatic
    fun getMD5(url: String?): String {
        try {
            return DigestUtils.md5Hex(url)
        } catch (e: Exception) {
        }
        return ""
    }
}
