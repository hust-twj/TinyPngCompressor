package com.husttwj.imagecompress.listener

import com.husttwj.imagecompress.util.ThreadUtils
import com.husttwj.imagecompress.ui.dialog.TinyImageDialog
import com.husttwj.imagecompress.util.FileUtils
import com.husttwj.imagecompress.util.LogUtil
import com.husttwj.imagecompress.util.TinyPng
import com.intellij.openapi.ui.Messages
import java.awt.event.ActionEvent
import java.io.File
import javax.swing.tree.DefaultTreeModel

class ProcessActionListener(dialog: TinyImageDialog) : ActionListenerBase(dialog) {

    override fun actionPerformed(e: ActionEvent) {

        dialog.compressInProgress = true
        dialog.btnProcess.isEnabled = false
        dialog.btnProcess.text = "Compressing"
        dialog.btnCancel.text = "Cancel"
        dialog.rootPane.defaultButton = dialog.btnCancel
        ThreadUtils.submit {
            var hasError: Boolean
            var repeatCount = 0
            var errorMessage = ""
            do {
                hasError = false
                var sleepTime: Long = 0
                var count = 0
                if (!File(FileUtils.sImageFileDirPath, dialog.processKey).exists()) {
                    File(FileUtils.sImageFileDirPath, dialog.processKey).mkdir()
                }
                for (node in dialog.imageFileNodes) {
                    try {
                        if (!node.isChecked || node.compressedImageFile != null) {
                            continue
                        }
                        node.error = null
                        if (sleepTime > 0) {
                            Thread.sleep(sleepTime)
                        }
                        count++
                        val uploadInfo = TinyPng.tinifyFile(dialog.processKey, File(node.virtualFile!!.path))
                        node.compressedImageFile = uploadInfo.output!!.file
                        if (count > 6 && sleepTime > 0) {
                            sleepTime -= 500
                            count = 0
                        }
                    } catch (throwable: Throwable) {
                        LogUtil.d("Compress image error", throwable)
                        if (sleepTime < 5000) {
                            count = 0
                            sleepTime += 500
                        }
                        node.error = throwable
                        hasError = true
                        errorMessage = (throwable.message ?: "")
                    }
                    ThreadUtils.runOnUIThread {
                        (dialog.fileTree.model as DefaultTreeModel).nodeChanged(node)
                        if (dialog.detailsAfter.text.trim().isEmpty()) {
                            dialog.mImageSelectListener.valueChanged(null)
                        }
                    }
                    if (!dialog.compressInProgress) {
                        break
                    }
                }
            } while (hasError && repeatCount++ < 3 && dialog.compressInProgress)
            if (hasError) {
                ThreadUtils.runOnUIThread {
                    dialog.onCompressFinish()
                    dialog.rootPane.defaultButton = dialog.btnProcess
                    Messages.showMessageDialog(
                        dialog.contentPane,
                        "Compress error:${errorMessage}",
                        "TinyPng Compress",
                        Messages.getInformationIcon()
                    )
                }
                return@submit
            }
            ThreadUtils.runOnUIThread {
                dialog.onCompressFinish()
                if (dialog.detailsAfter.text.trim().isEmpty()) {
                    dialog.mImageSelectListener.valueChanged(null)
                }
            }
        }
    }

}