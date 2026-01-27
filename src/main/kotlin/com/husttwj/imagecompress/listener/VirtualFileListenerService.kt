package com.husttwj.imagecompress.listener


import com.husttwj.imagecompress.action.BaseAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileCopyEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.util.messages.MessageBusConnection

@Service(Service.Level.PROJECT)
class VirtualFileListenerService(private val project: Project) {


    private var connection: MessageBusConnection? = null

    /**
     * observer VFileCreateEvent and VFileCopyEvent
     */
    fun startListening() {
        if (connection != null) return

        connection = project.messageBus.connect()

        connection?.subscribe(VirtualFileManager.VFS_CHANGES, object : BulkFileListener {
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


    /**
     * stop observer VFileCreateEvent and VFileCopyEvent
     */
    fun stopListening() {
        connection?.disconnect()
        connection = null
    }
}