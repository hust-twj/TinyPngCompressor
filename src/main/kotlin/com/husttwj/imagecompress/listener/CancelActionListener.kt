package com.husttwj.imagecompress.listener


import com.husttwj.imagecompress.ui.dialog.TinyImageDialog
import java.awt.event.ActionEvent

class CancelActionListener(dialog: TinyImageDialog) : ActionListenerBase(dialog) {

    override fun actionPerformed(e: ActionEvent) {
        val isInProgress = dialog.compressInProgress
        if (!isInProgress) {
            dialog.dispose()
        }
        dialog.compressInProgress = false
        //todo 文案
        dialog.btnCancel.text = "取消"
    }

}