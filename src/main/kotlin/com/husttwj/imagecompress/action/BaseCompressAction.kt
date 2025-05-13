package com.husttwj.imagecompress.action

import com.husttwj.imagecompress.ui.dialog.TinyImageDialog
import com.husttwj.imagecompress.util.FileUtils
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.WindowManager
import java.util.function.Predicate

/**
 * register right-click action
 */
class CompressAction : AnAction() {

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
        // 根据当前选中的文件判断是否显示此动作
        val files: Array<VirtualFile> = event.getData(com.intellij.openapi.actionSystem.CommonDataKeys.VIRTUAL_FILE_ARRAY) ?: return
        val show = files.all {
            sSupportedImageType.contains(it.extension?.toLowerCase()?: "") && !it.name.toLowerCase().endsWith(".9.png")
        }
        event.presentation.isVisible = show
    }

    companion object {
        private val sSupportedImageType =  listOf("png", "webp", "jpg", "jpeg")

        @JvmField
        var sPredicate =
            Predicate<VirtualFile> { virtualFile ->
                if (virtualFile.extension == null) {
                    false
                } else {
                    !virtualFile.path.contains("build/intermediates/")
                            && sSupportedImageType.contains(virtualFile.extension!!.toLowerCase())
                            && !virtualFile.name.toLowerCase().endsWith(".9.png")
                            && !virtualFile.name.toLowerCase().endsWith(".9.webp")
                }
            }
    }
}