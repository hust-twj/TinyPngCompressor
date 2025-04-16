package com.husttwj.imagecompress.listener

import com.husttwj.imagecompress.util.FileUtils

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity


class CompressStartupActivity : StartupActivity.DumbAware {

    override fun runActivity(project: Project) {
        FileUtils.init()
    }

}

