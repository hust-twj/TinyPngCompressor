package com.husttwj.imagecompress.util


import com.luciad.imageio.webp.WebPReadParam
import java.awt.Image
import java.io.File
import java.io.InputStream
import javax.imageio.ImageIO
import javax.imageio.stream.FileImageInputStream

object ImageUtils {

    @JvmStatic
    fun getImage(file: File?): Image? {
        file ?: return null
        return if (file.name.endsWith("webp")) {
            val reader = ImageIO.getImageReadersByMIMEType("image/webp").next()
            val readParam = WebPReadParam()
            readParam.isBypassFiltering = true
            reader.input = FileImageInputStream(file)
            reader.read(0, readParam)
        } else {
            ImageIO.read(file)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun getImage(inputStream: InputStream?, isWebP: Boolean = false): Image? {
        if (isWebP) {
            val reader = ImageIO.getImageReadersByMIMEType("image/webp").next()
            val readParam = WebPReadParam()
            readParam.isBypassFiltering = true
            reader.input = inputStream
            return reader.read(0, readParam)
        } else {
            return ImageIO.read(inputStream)
        }
    }


}