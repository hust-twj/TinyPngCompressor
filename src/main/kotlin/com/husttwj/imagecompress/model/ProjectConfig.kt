package com.husttwj.imagecompress.model


class ProjectConfig {

    private var tinyUrl: String? = null

    private var tinifyApiKeys: MutableList<TinifyApiKeyConfig>? = null

    @JvmField
    val tinyHeadName: List<String>? = null

    @JvmField
    val tinyHeadValue: List<String>? = null

    /**
     * setting->tools
     */
    private var autoDetectImage: Boolean = true

    /**
     * compress dialog -> Convert to WebP checkbox
     */
    private var convertToWebpEnabled: Boolean = false


    fun getTinyUrl(): String {
        if (tinyUrl == null || tinyUrl!!.isEmpty()) {
            tinyUrl = "https://tinypng.com/backend/opt/shrink"
        }
        return tinyUrl!!
    }

    fun isAutoDetectImage(): Boolean {
        return autoDetectImage
    }

    fun setAutoDetectImage(autoDetectImage: Boolean) {
        this.autoDetectImage = autoDetectImage
    }

    fun isConvertToWebpEnabled(): Boolean {
        return convertToWebpEnabled
    }

    fun setConvertToWebpEnabled(convertToWebpEnabled: Boolean) {
        this.convertToWebpEnabled = convertToWebpEnabled
    }

    fun getTinifyApiKeys(): MutableList<TinifyApiKeyConfig> {
        if (tinifyApiKeys == null) {
            tinifyApiKeys = mutableListOf()
        }
        return tinifyApiKeys!!
    }

    fun setTinifyApiKeys(tinifyApiKeys: MutableList<TinifyApiKeyConfig>?) {
        this.tinifyApiKeys = tinifyApiKeys ?: mutableListOf()
    }
}
