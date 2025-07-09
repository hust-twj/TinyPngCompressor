package com.husttwj.imagecompress.action

import com.husttwj.imagecompress.ui.dialog.TinyImageDialog
import com.husttwj.imagecompress.util.FileUtils
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.WindowManager
import java.util.function.Predicate

abstract class BaseAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {

        val project = e.project ?: return

        val roots = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(e.dataContext) ?: return
        val list = FileUtils.getMatchFileList(roots, sPredicate, false)
        val frame = WindowManager.getInstance().getFrame(project) ?: return

        val dialog = TinyImageDialog(project, list, listOf(*roots), false, null, null)
        dialog.setDialogSize(frame)
        dialog.isVisible = true
        dialog.isAlwaysOnTop = false
    }

    companion object {

        val sSupportedImageType =  listOf("png", "webp", "jpg", "jpeg")

        @JvmField
        var sPredicate =
            Predicate<VirtualFile> { virtualFile ->
                if (virtualFile.extension == null) {
                    false
                } else {
                    !virtualFile.path.contains("build/intermediates/")
                            && sSupportedImageType.contains(virtualFile.extension!!.lowercase())
                            && !virtualFile.name.lowercase().endsWith(".9.png")
                            && !virtualFile.name.lowercase().endsWith(".9.webp")
                }
            }
    }
}