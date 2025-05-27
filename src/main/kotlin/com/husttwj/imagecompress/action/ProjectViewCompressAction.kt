package com.husttwj.imagecompress.action

import com.husttwj.imagecompress.ui.dialog.TinyImageDialog
import com.husttwj.imagecompress.util.FileUtils
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.WindowManager

/**
 * register right-click action in ProjectView
 */
class ProjectViewCompressAction : BaseAction() {

    override fun actionPerformed(e: AnActionEvent) {

        val project = e.project

        val roots = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(e.dataContext) ?: return
        val list = FileUtils.getMatchFileList(roots, sPredicate, false)
        val frame = WindowManager.getInstance().getFrame(project) ?: return

        val dialog = TinyImageDialog(project!!, list, listOf(*roots), false, null, null)
        dialog.setDialogSize(frame)
        dialog.isVisible = true
        dialog.isAlwaysOnTop = false
    }

    override fun update(event: AnActionEvent) {
        val files: Array<VirtualFile> = event.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY) ?: return
        val show = files.all {
            sSupportedImageType.contains(it.extension?.lowercase()?: "") && !it.name.lowercase().endsWith(".9.png")
        }
        event.presentation.isVisible = show
    }

}