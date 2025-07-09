package com.husttwj.imagecompress.listener

import com.husttwj.imagecompress.ui.dialog.TinyImageDialog
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.WindowManager
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Handles image files that are newly added to the project (e.g., via copy and paste),
 * and schedules a compress dialog to process them.
 *
 * This class batches incoming image files and shows the TinyImageDialog after a short delay,
 * to avoid spamming the UI when multiple files are added at once.
 */
object ImageFilePasteHandler {

    private val projectPendingFiles = ConcurrentHashMap<Project, CopyOnWriteArrayList<VirtualFile>>()

    private val scheduler = Executors.newSingleThreadScheduledExecutor()
    private val scheduledTasks = ConcurrentHashMap<Project, ScheduledFuture<*>>()

    /**
     * Called when a new image file is detected (e.g., pasted into the project).
     * Adds it to the pending list and schedules a dialog to handle compression.
     */
    fun onImageFileDetected(project: Project, copiedFile: VirtualFile) {
        val list = projectPendingFiles.getOrPut(project) { CopyOnWriteArrayList() }
        list.add(copiedFile)
        scheduleCompressDialog(project)
    }

    /**
     * Schedules a delayed compression dialog to handle all pending files.
     */
    private fun scheduleCompressDialog(project: Project) {
        val existingTask = scheduledTasks[project]
        if (existingTask == null || existingTask.isDone) {
            val task = scheduler.schedule({
                val files = projectPendingFiles.remove(project)?.toList().orEmpty()
                if (files.isNotEmpty()) {
                    showCompressDialog(project, files)
                }
            }, 1000, TimeUnit.MILLISECONDS)
            scheduledTasks[project] = task
        }
    }

    /**
     * Display the TinyImageDialog  to preview and compress the image files.
     */
    private fun showCompressDialog(project: Project, files: List<VirtualFile>) {
        ApplicationManager.getApplication().invokeLater {
            WindowManager.getInstance().getFrame(project)?.let { frame ->

                //showInfoMessage(project, files.firstOrNull()?.path?: "empty")
                val dialog = TinyImageDialog(project, files, files, true, null, null)
                dialog.setDialogSize(frame)
                dialog.isVisible = true
                dialog.isAlwaysOnTop = false
            }

        }
    }

}