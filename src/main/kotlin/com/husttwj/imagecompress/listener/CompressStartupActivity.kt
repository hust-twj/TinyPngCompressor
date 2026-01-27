package com.husttwj.imagecompress.listener

import com.husttwj.imagecompress.util.FileUtils
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.ui.Messages


/**
 * Use StartupActivity or ProjectActivity for init
 */
class CompressStartupActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        FileUtils.init()

        val config = FileUtils.getConfig()
        if (config.isAutoDetectImage()) {
            project.getService(VirtualFileListenerService::class.java).startListening()
        }
    }

}

fun showInfoMessage(project: Project, content: String) {
    Messages.showInfoMessage(
        project,
        content,
        "TinyPngCompressor"
    )
}

