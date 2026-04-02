package com.husttwj.imagecompress.util

import com.husttwj.imagecompress.model.OutputInfo
import com.husttwj.imagecompress.model.ProjectConfig
import com.husttwj.imagecompress.model.TinifyApiKeyConfig
import com.husttwj.imagecompress.model.UploadInfo
import com.tinify.Options
import com.tinify.Source
import com.tinify.Tinify
import java.io.File

object TinyPngV2 {

    private val tinifyLock = Any()
    private val webpConvertibleTypes = setOf("png", "jpg", "jpeg")

    /**
     * @param convertToWebp true:convert png to webp if it can be smaller and then compress; false: only compress without format conversion
     */
    fun tinifyFile(parent: String, sourceFile: File, convertToWebp: Boolean): UploadInfo? {
        if (!sourceFile.exists() || sourceFile.isDirectory) {
            LogUtil.d("TinyPngV2. compress failed, sourceFile:${sourceFile.exists()}|${sourceFile.isDirectory}")
            return null
        }
        val config = FileUtils.getConfig()
        val activeKey = validateAndPickActiveKey(config) ?: return null
        return try {
            compressWithKey(parent, sourceFile, activeKey.apiKey, convertToWebp)
        } catch (throwable: Throwable) {
            LogUtil.e("TinyPngV2. compress failed, fallback to web mode, msg:${throwable.message}")
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
            LogUtil.d("TinyPngV2. Tinify validate success")
            true
        } catch (throwable: Throwable) {
            LogUtil.e("TinyPngV2. Tinify validate failed", throwable)
            false
        }
    }

    private fun compressWithKey(parent: String, sourceFile: File, apiKey: String, convertToWebp: Boolean): UploadInfo {
        val extension = sourceFile.extension.lowercase()
        if (convertToWebp && webpConvertibleTypes.contains(extension)) {
            LogUtil.d("TinyPngV2. try convert $extension to webp, file=${sourceFile.name}")
            val converted = convertToWebpIfSmaller(parent, sourceFile, apiKey)
            if (converted != null) {
                LogUtil.d("TinyPngV2. convert $extension to webp success, file=${converted.output?.file?.name}")
                return converted
            }
            LogUtil.d("TinyPngV2. convert $extension to webp skipped or discarded, continue normal compress, file=${sourceFile.name}")
        } else if (convertToWebp) {
            LogUtil.d("TinyPngV2. convertToWebp skipped: unsupported source type=$extension, file=${sourceFile.name}")
        }
        return compressPreparedFile(parent, sourceFile, apiKey, sourceFile.extension.ifEmpty { "png" })
    }

    private fun convertToWebpIfSmaller(parent: String, sourceFile: File, apiKey: String): UploadInfo? {
        val fileName = buildCacheName(sourceFile)
        val convertedWebp = File(FileUtils.sImageFileDirPath, parent + File.separator + fileName + ".webp.converted")
        convertedWebp.parentFile?.mkdirs()
        synchronized(tinifyLock) {
            Tinify.setKey(apiKey)
            val source: Source = Tinify.fromFile(sourceFile.absolutePath)
            val converted = source.convert(
                Options().with("type", arrayOf("image/png", "image/webp"))
            ).result()
            converted.toFile(convertedWebp.absolutePath)
            LogUtil.d("TinyPngV2. sdk convert result extension=${converted.extension()}, size=${converted.size()}, file=${sourceFile.name}")
        }
        if (convertedWebp.length() >= sourceFile.length()) {
            LogUtil.d("TinyPngV2. discard converted webp because it is not smaller, source=${sourceFile.length()}, webp=${convertedWebp.length()}, file=${sourceFile.name}")
            FileUtils.deleteFile(convertedWebp)
            return null
        }
        LogUtil.d("TinyPngV2. converted webp is smaller, source=${sourceFile.length()}, webp=${convertedWebp.length()}, file=${sourceFile.name}")

        val compressedWebp = compressPreparedFile(parent, convertedWebp, apiKey, "webp")
        val compressedFile = compressedWebp.output?.file
        if (compressedFile != null && compressedFile.length() > convertedWebp.length()) {
            LogUtil.d("TinyPngV2. compressed webp is larger than converted webp, keep converted webp, converted=${convertedWebp.length()}, compressed=${compressedFile.length()}, file=${sourceFile.name}")
            val finalWebp = File(FileUtils.sImageFileDirPath, parent + File.separator + fileName + ".webp")
            if (finalWebp.exists()) {
                FileUtils.deleteFile(finalWebp)
            }
            convertedWebp.renameTo(finalWebp)
            compressedWebp.output?.file = finalWebp
            compressedWebp.output?.size = finalWebp.length().toInt()
            return compressedWebp
        }
        FileUtils.deleteFile(convertedWebp)
        return compressedWebp
    }

    private fun compressPreparedFile(parent: String, sourceFile: File, apiKey: String, fileType: String): UploadInfo {
        val fileName = buildCacheName(sourceFile)
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
        outputInfo.type = fileType
        val uploadInfo = UploadInfo()
        uploadInfo.output = outputInfo
        return uploadInfo
    }

    private fun buildCacheName(sourceFile: File): String {
        return MD5Utils.getMD5(sourceFile.absolutePath + sourceFile.lastModified() + sourceFile.length())
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
