package com.husttwj.imagecompress.ui.components

import com.husttwj.imagecompress.util.ImageUtils
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.JBColor
import com.intellij.util.ui.UIUtil
import java.awt.Color
import java.awt.Graphics
import java.awt.Image
import java.awt.Rectangle
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.swing.BorderFactory
import javax.swing.JPanel
import kotlin.math.floor

class JImage : JPanel() {

    var imageSize: Long = 0

    private var image: BufferedImage? = null

    private var bgImage: BufferedImage? = null

    private var myRect: Rectangle? = null

    @Throws(IOException::class)
    fun setImage(file: VirtualFile?) {
        setImageInner(if (file == null) null else File(file.path))
    }

    @Throws(IOException::class)
    fun setImage(file: File?) {
        setImageInner(file)
    }

    @Throws(IOException::class)
    private fun setImageInner(file: File?) {
        // LogUtil.d("setImageInner: name=${(file?.name)}  null=${(file == null )} exists=${file?.exists()}  path= ${file?.path}")
        if (file == null || !file.exists()) {
            image = null
            imageSize = 0
        } else {
            // LogUtil.d("setImageInner->getImage start: ")
            image = ImageUtils.getImage(file) as? BufferedImage?
            // LogUtil.d("setImageInner->getImage end:  name=${(file.name)}  null=${image == null}  width=${image?.width}   height=${image?.height}  path= ${file.path}")
            imageSize = file.length()
        }
        if (image != null) {
            myRect = imageRect
            bgImage = prepareChessboardBackground()
        }
        repaint()
    }

    fun getImage(): Image? {
        return image
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        if (image != null) {
            val myRect = myRect ?: return
            g.drawImage(bgImage, myRect.x, myRect.y, myRect.width, myRect.height, this)
            g.drawImage(image, myRect.x, myRect.y, myRect.width, myRect.height, this)
        }
    }

    private val imageRect: Rectangle?
        get() {
           val image = image ?: return null
            val rect = Rectangle()
            if (width > image.width && height > image.height) {
                rect.width = image.width
                rect.height = image.height
                rect.x = (width - rect.width) / 2
                rect.y = (height - rect.height) / 2
                return rect
            }
            val widthTransform = width.toFloat() / image.width.toFloat()
            val heightTransform = height.toFloat() / image.height.toFloat()
            if (width < image.width && height < image.height) {
                if (widthTransform < heightTransform) {
                    rect.width = floor(image.width * widthTransform.toDouble()).toInt()
                    rect.height = floor(image.height * widthTransform.toDouble()).toInt()
                } else {
                    rect.width = floor(image.width * heightTransform.toDouble()).toInt()
                    rect.height = floor(image.height * heightTransform.toDouble()).toInt()
                }
                rect.x = (width - rect.width) / 2
                rect.y = (height - rect.height) / 2
                return rect
            }
            if (width > image.width) {
                rect.width = floor(image.width * heightTransform.toDouble()).toInt()
                rect.height = height
                rect.x = (width - rect.width) / 2
                rect.y = 0
            } else {
                rect.width = width
                rect.height = floor(image.height * widthTransform.toDouble()).toInt()
                rect.x = 0
                rect.y = (height - rect.height) / 2
            }
            return rect
        }

    private fun prepareChessboardBackground(): BufferedImage? {
        val myRect = myRect ?: return null
        val image = UIUtil.createImage(myRect.width, myRect.height, BufferedImage.TYPE_INT_RGB)
        val graphics = image.graphics
        var even = true
        graphics.color = Color.WHITE
        graphics.fillRect(0, 0, myRect.width, myRect.height)
        graphics.color = Color.LIGHT_GRAY
        run {
            var x = 0
            while (x < myRect.width) {
                even = x / 5 and 1 == 0
                run {
                    var y = 0
                    while (y < myRect.height) {
                        even = !even
                        if (even) {
                            graphics.fillRect(x, y, 5, 5)
                        }
                        y += 5
                    }
                }
                x += 5
            }
        }
        return image
    }

    init {
        border = BorderFactory.createLineBorder(JBColor.border())

        addComponentListener(object : ComponentListener {
            override fun componentResized(e: ComponentEvent) {
                if (image != null) {
                    val newRect = imageRect
                    if (myRect != newRect) {
                        myRect = imageRect
                        bgImage = prepareChessboardBackground()
                    }
                }
            }

            override fun componentMoved(e: ComponentEvent) {}
            override fun componentShown(e: ComponentEvent) {}
            override fun componentHidden(e: ComponentEvent) {}
        })
    }
}