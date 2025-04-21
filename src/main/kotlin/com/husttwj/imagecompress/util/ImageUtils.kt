package com.husttwj.imagecompress.util


import java.awt.Image
import java.io.File

import javax.imageio.ImageIO

object ImageUtils {

    @JvmStatic
    fun getImage(file: File?): Image? {

        try {
            // 如果是png，这里的 ImageIO.read 方法借助 twelvemonkeys 库来读取 WebP 文件
            return ImageIO.read(file)
        } catch (e: Exception ) {
            LogUtil.d("getImage Exception: " +e.message)
            return null;
        }
    }


}