package com.husttwj.imagecompress.util

import com.husttwj.imagecompress.model.OutputInfo
import com.husttwj.imagecompress.model.ProjectConfig
import com.husttwj.imagecompress.model.TinifyApiKeyConfig
import com.husttwj.imagecompress.model.UploadInfo
import com.tinify.Tinify
import java.io.File

object TinyPngV2 {

    private val tinifyLock = Any()

    fun tinifyFile(parent: String, sourceFile: File): UploadInfo? {
        if (!sourceFile.exists() || sourceFile.isDirectory) {
            return null
        }
        val config = FileUtils.getConfig()
        val activeKey = validateAndPickActiveKey(config) ?: return null
        return try {
            compressWithKey(parent, sourceFile, activeKey.apiKey)
        } catch (throwable: Throwable) {
            LogUtil.d("TinyPngV2 compress failed, fallback to web mode", throwable)
            markKeyStatus(config, activeKey.apiKey, false)
            null
        }
    }

    fun refreshStatuses(config: ProjectConfig): Boolean {
        var changed = false
        val keys = config.getTinifyApiKeys()
        for (key in keys) {
            val apiKey = key.apiKey.trim()
            if (apiKey.isEmpty()) {
                if (key.active || key.lastValidatedAt != 0L) {
                    key.active = false
                    key.lastValidatedAt = 0L
                    changed = true
                }
                continue
            }
            val isActive = validateKey(apiKey)
            if (key.active != isActive) {
                key.active = isActive
                changed = true
            }
            val now = System.currentTimeMillis()
            if (key.lastValidatedAt != now) {
                key.lastValidatedAt = now
                changed = true
            }
        }
        return changed
    }

    private fun validateAndPickActiveKey(config: ProjectConfig): TinifyApiKeyConfig? {
        val keys = config.getTinifyApiKeys()
        if (keys.isEmpty()) {
            return null
        }
        var changed = false
        var selected: TinifyApiKeyConfig? = null
        for (key in keys) {
            val apiKey = key.apiKey.trim()
            if (apiKey.isEmpty()) {
                if (key.active || key.lastValidatedAt != 0L) {
                    key.active = false
                    key.lastValidatedAt = 0L
                    changed = true
                }
                continue
            }
            val isActive = validateKey(apiKey)
            if (key.active != isActive) {
                key.active = isActive
                changed = true
            }
            val now = System.currentTimeMillis()
            if (key.lastValidatedAt != now) {
                key.lastValidatedAt = now
                changed = true
            }
            if (isActive && selected == null) {
                selected = key
            }
        }
        if (changed) {
            FileUtils.saveConfig(config)
        }
        return selected
    }

    private fun validateKey(apiKey: String): Boolean {
        return try {
            synchronized(tinifyLock) {
                Tinify.setKey(apiKey)
                Tinify.validate()
            }
            true
        } catch (throwable: Throwable) {
            LogUtil.e("Tinify validate failed", throwable)
            false
        }
    }

    private fun compressWithKey(parent: String, sourceFile: File, apiKey: String): UploadInfo {
        val fileType = sourceFile.extension.ifEmpty { "png" }
        val fileName = MD5Utils.getMD5(sourceFile.absolutePath + sourceFile.lastModified() + sourceFile.length())
        val tmpFile = File(FileUtils.sImageFileDirPath, parent + File.separator + fileName + "." + fileType + ".tmp")
        tmpFile.parentFile?.mkdirs()
        synchronized(tinifyLock) {
            Tinify.setKey(apiKey)
            Tinify.fromFile(sourceFile.absolutePath).toFile(tmpFile.absolutePath)
        }
        val convertedFile = File(FileUtils.sImageFileDirPath, parent + File.separator + fileName + "." + fileType)
        if (convertedFile.exists()) {
            FileUtils.deleteFile(convertedFile)
        }
        tmpFile.renameTo(convertedFile)

        val outputInfo = OutputInfo()
        outputInfo.file = convertedFile
        outputInfo.size = convertedFile.length().toInt()
        outputInfo.type = sourceFile.extension
        val uploadInfo = UploadInfo()
        uploadInfo.output = outputInfo
        return uploadInfo
    }

    private fun markKeyStatus(config: ProjectConfig, apiKey: String, active: Boolean) {
        val now = System.currentTimeMillis()
        var changed = false
        for (key in config.getTinifyApiKeys()) {
            if (key.apiKey.trim() == apiKey.trim()) {
                if (key.active != active) {
                    key.active = active
                    changed = true
                }
                if (key.lastValidatedAt != now) {
                    key.lastValidatedAt = now
                    changed = true
                }
            }
        }
        if (changed) {
            FileUtils.saveConfig(config)
        }
    }
}
