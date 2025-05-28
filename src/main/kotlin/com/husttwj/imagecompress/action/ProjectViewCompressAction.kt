package com.husttwj.imagecompress.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.vfs.VirtualFile

/**
 * register right-click action in ProjectView
 */
class ProjectViewCompressAction : BaseAction() {


    override fun update(event: AnActionEvent) {
        super.update(event)

        //todo: VIRTUAL_FILE_ARRAY return null
        val files: Array<VirtualFile> = event.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY) ?: return
        val show = files.all {
            sSupportedImageType.contains(it.extension?.lowercase()?: "") && !it.name.lowercase().endsWith(".9.png")
        }
        event.presentation.isVisible = show
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

}