package com.husttwj.imagecompress.ui.settings

import com.husttwj.imagecompress.model.ProjectConfig
import com.husttwj.imagecompress.util.FileUtils
import com.husttwj.imagecompress.util.GsonUtils
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.panel
import java.io.File
import javax.swing.JComponent
import com.intellij.openapi.project.Project
import com.husttwj.imagecompress.listener.VirtualFileListenerService

/**
 * Setting->tools
 * 支持开启或者关闭自动检测复制的图片，并立即生效
 */
class TinyPngSettings(private val project: Project) : Configurable {

    private lateinit var autoDetectImageCheckBox: JBCheckBox

    private var config: ProjectConfig? = null

    override fun getDisplayName(): String = "TinyPngCompressor"

    override fun getPreferredFocusedComponent(): JComponent? = autoDetectImageCheckBox

    override fun createComponent(): JComponent {
        // 初始化配置
        config = FileUtils.getConfig()

        // 创建复选框
        autoDetectImageCheckBox = JBCheckBox(
            TinyPngBundle.message("settings.autoDetectImageOption"),
            config?.isAutoDetectImage() ?: true
        )

        // DSL 创建设置面板
        return panel {
            row {
                cell(autoDetectImageCheckBox)
            }
        }
    }

    override fun isModified(): Boolean {
        return autoDetectImageCheckBox.isSelected != config?.isAutoDetectImage()
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        val newValue = autoDetectImageCheckBox.isSelected

        // 更新内存中的配置
        config?.setAutoDetectImage(newValue)

        // 持久化到文件
        val configFile = File(FileUtils.sMainDirPath, FileUtils.CONFIG_INFO_FILE_NAME)
        try {
            val configContent = GsonUtils.sGson.toJson(config)
            FileUtils.writeFileContent(configFile, configContent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 立即刷新全局配置，确保 FileUtils.getConfig() 返回最新配置
        FileUtils.resetConfig()

        // 获取 Service 并根据新状态决定开启或关闭监听
        val listenerService = project.getService(VirtualFileListenerService::class.java)
        if (newValue) {
            listenerService.startListening()
        } else {
            listenerService.stopListening()
        }
    }

    override fun reset() {
        // 重置 UI 状态
        config = FileUtils.getConfig()
        autoDetectImageCheckBox.isSelected = config?.isAutoDetectImage() ?: true
    }

    override fun disposeUIResources() {
        config = null
    }
}
