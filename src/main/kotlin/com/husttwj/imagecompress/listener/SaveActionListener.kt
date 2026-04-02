package com.husttwj.imagecompress.listener


import com.husttwj.imagecompress.ui.dialog.TinyImageDialog
import com.husttwj.imagecompress.util.FileUtils
import com.husttwj.imagecompress.util.LogUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.vfs.VirtualFile
import java.awt.event.ActionEvent
import java.io.IOException

class SaveActionListener(dialog: TinyImageDialog) : ActionListenerBase(dialog) {

    override fun actionPerformed(e: ActionEvent) {
        dialog.btnSave.isEnabled = false
        dialog.btnCancel.isEnabled = false
        ApplicationManager.getApplication().runWriteAction(object : Runnable {
            override fun run() {
                for (node in dialog.imageFileNodes) {
                    try {
                        if (!node.isChecked || node.compressedImageFile == null) {
                            continue
                        }
                        if (node.compressedImageFile!!.length() < node.virtualFile!!.length) {
                            saveCompressedFile(node.virtualFile!!, node.compressedImageFile!!)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                dialog.dispose()
            }
        })
    }

    private fun saveCompressedFile(targetFile: VirtualFile, compressedFile: java.io.File) {
        val sourceExtension = targetFile.extension?.lowercase().orEmpty()
        val targetExtension = compressedFile.extension.lowercase()
        val bytes = FileUtils.getFileContentBytes(compressedFile) ?: return
        if (sourceExtension == targetExtension) {
            val stream = targetFile.getOutputStream(this)
            stream.write(bytes)
            stream.close()
            LogUtil.d("SaveAction. overwrite source file success, file=${targetFile.path}")
            return
        }

        val parent = targetFile.parent ?: return
        val webpName = targetFile.nameWithoutExtension + "." + targetExtension
        val newFile = parent.findChild(webpName) ?: parent.createChildData(this, webpName)
        val outputStream = newFile.getOutputStream(this)
        outputStream.write(bytes)
        outputStream.close()
        LogUtil.d("SaveAction. create converted file success, file=${newFile.path}")
        targetFile.delete(this)
        LogUtil.d("SaveAction. delete original file after webp conversion, file=${targetFile.path}")
    }
}