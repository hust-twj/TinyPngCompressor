package com.husttwj.imagecompress.listener

import com.husttwj.imagecompress.action.BaseAction
import com.husttwj.imagecompress.util.FileUtils
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.*
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileCopyEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent


/**
 * Use StartupActivity or ProjectActivity for init
 */
class CompressStartupActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        FileUtils.init()

        registerVirtualFileListener(project)
    }

    /**
     * observer VFileCreateEvent and VFileCopyEvent
     */
    private fun registerVirtualFileListener(project: Project) {
        project.messageBus.connect().subscribe(VirtualFileManager.VFS_CHANGES, object : BulkFileListener {
            override fun after(events: List<VFileEvent>) {
                for (event in events) {
                    if (event is VFileCopyEvent) {
                        val originalFile = event.file
                        //Ignore images copied within the same project.
                        if (isFileInProject(project, originalFile)) continue

                        //find copied image file
                        val copiedFile = event.newParent.findChild(event.newChildName) ?: continue
                        // When multiple projects are open, only the target project will respond;
                        // all other projects will ignore the event.
                        if (!isFileInProject(project, copiedFile)) continue

                        if (BaseAction.sPredicate.test(copiedFile)) {
                            ImageFilePasteHandler.onImageFileDetected(project, copiedFile)
                        }
                    }
                }
            }
        })
    }

    private fun isFileInProject(project: Project, file: VirtualFile): Boolean {
        val basePath = project.basePath ?: return false
        return FileUtil.isAncestor(basePath, file.path, false)
    }

}

fun showInfoMessage(project: Project, content: String) {
    Messages.showInfoMessage(
        project,
        content,
        "TinyPngCompressor"
    )
}

