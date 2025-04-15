package com.husttwj.imagecompress.listener


import com.husttwj.imagecompress.ui.dialog.TinyImageDialog
import com.husttwj.imagecompress.util.FileUtils
import com.intellij.openapi.application.ApplicationManager
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
                            val stream = node.virtualFile!!.getOutputStream(this)
                            stream.write(FileUtils.getFileContentBytes(node.compressedImageFile))
                            stream.close()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                dialog.dispose()
            }
        })
    }
}