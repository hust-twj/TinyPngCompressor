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

/**
 * 右键压缩，出现图片压缩弹窗
 */
class CompressAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
//        val project = e.project
//        val roots = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(e.dataContext) as? Array<VirtualFile> ?: return
//        val frame = WindowManager.getInstance().getFrame(project) ?: return
////        if (StringUtil.isEmptyOrSpaces(Tinify.key())) {
////            val settings = PluginGlobalSettings.getInstance()
////            if (StringUtil.isEmptyOrSpaces(settings.apiKey)) {
////                settings.apiKey = Messages.showInputDialog(
////                    project,
////                    "What's your TinyPNG API Key?",
////                    "API Key",
////                    Messages.getQuestionIcon()
////                )
////            }
////            if (StringUtil.isEmptyOrSpaces(settings.apiKey)) {
////                return
////            } else {
////                Tinify.setKey(settings.apiKey)
////            }
////        }
//       // val list = getSupportedFileList(roots)
//        val dialog = TinyImageDialog(project, listOf(*roots), list, false, null, null)
//        dialog.setDialogSize(frame)
//        dialog.isVisible = true


//        LogUtil.d(">>>>>>> actionPerformed >>>>>>>")

        val project = e.project

        val roots = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(e.dataContext) ?: return
        val list = FileUtils.getMatchFileList(roots, sPredicate, false)
        val frame = WindowManager.getInstance().getFrame(project) ?: return
        LogUtil.d(">>>>>>> show TinyImageDialog >>>>>>>")
        val dialog = TinyImageDialog(project!!, list, listOf(*roots), false, null, null)
        dialog.setDialogSize(frame)
        dialog.isVisible = true
        dialog.isAlwaysOnTop = false
    }

    override fun update(event: AnActionEvent) {
        LogUtil.d(">>>>>>> update >>>>>>>")
        // 根据当前选中的文件判断是否显示此动作
        val files: Array<VirtualFile> = event.getData(com.intellij.openapi.actionSystem.CommonDataKeys.VIRTUAL_FILE_ARRAY) ?: return
        val show = files.all {
            val name = it.name.toLowerCase()
            name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")
        }
        event.presentation.isVisible = show
    }

//    private fun getSupportedFileList(files: Array<VirtualFile>?): List<VirtualFile> {
//        val result: MutableList<VirtualFile> = LinkedList()
//        if (files == null) {
//            return result
//        }
//        for (file in files) {
//            if (file.isDirectory) {
//                result.addAll(getSupportedFileList(file.children))
//            } else {
//                val extension = file.extension
//                if (extension != null && ArrayUtil.contains(
//                        extension.lowercase(Locale.getDefault()),
//                        *supportedExtensions
//                    )
//                ) {
//                    result.add(file)
//                }
//            }
//        }
//        return result
//    }

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