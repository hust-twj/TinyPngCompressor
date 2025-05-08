package com.husttwj.imagecompress.start

import com.husttwj.imagecompress.util.FileUtils

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity


/**
 * 使用 StartupActivity 或 ProjectActivity， 在应用启动时执行初始化逻辑
 */
class CompressStartupActivity : ProjectActivity {


    override suspend fun execute(project: Project) {
        FileUtils.init()
    }

}

