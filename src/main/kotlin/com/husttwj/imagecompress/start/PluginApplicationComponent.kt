package com.husttwj.imagecompress.start

import com.husttwj.imagecompress.util.FileUtils
import com.intellij.openapi.components.ApplicationComponent

/**
 * Support older IDE
 */
@Suppress("DEPRECATION")
class PluginApplicationComponent: ApplicationComponent {

    @Deprecated("Deprecated")
    override fun initComponent() {
        super.initComponent()

        FileUtils.init()
    }


}