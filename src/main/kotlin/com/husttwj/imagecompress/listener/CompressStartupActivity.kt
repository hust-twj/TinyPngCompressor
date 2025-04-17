package com.husttwj.imagecompress.listener

import com.husttwj.imagecompress.util.FileUtils

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity


/**
 * 使用 StartupActivity 或 ProjectActivity， 在应用启动时执行初始化逻辑
 */
class CompressStartupActivity : StartupActivity {

    override fun runActivity(project: Project) {
        FileUtils.init()
    }

}

