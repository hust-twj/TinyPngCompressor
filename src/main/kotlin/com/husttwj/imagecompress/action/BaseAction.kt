package com.husttwj.imagecompress.action

import com.husttwj.imagecompress.ui.dialog.TinyImageDialog
import com.husttwj.imagecompress.util.FileUtils
import com.husttwj.imagecompress.util.LogUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.WindowManager
import java.util.function.Predicate

abstract class BaseAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {

        val project = e.project
        if (project == null) {
            LogUtil.e("Compress action aborted: project is null")
            return
        }

        val roots = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(e.dataContext)
        if (roots == null) {
            LogUtil.e("Compress action aborted: roots is null")
            return
        }
        val list = FileUtils.getMatchFileList(roots, sPredicate, false)
        if (list.isEmpty()) {
            LogUtil.e("Compress action aborted: no supported image files, place=${e.place}")
            return
        }

        val frame = WindowManager.getInstance().getFrame(project)
        if (frame == null) {
            LogUtil.e("Compress action aborted: project frame is null, project=${project.name}")
            return
        }
        val dialog = TinyImageDialog(project, frame, list, listOf(*roots), false, null, null)
        dialog.setDialogSize(frame)
        dialog.isVisible = true
        dialog.isAlwaysOnTop = false
        dialog.showWithOwner()
        LogUtil.d("Compress dialog shown, imageCount=${list.size}, project=${project.name}, showing=${dialog.isShowing}, visible=${dialog.isVisible}")
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