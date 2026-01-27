package com.husttwj.imagecompress.model


class ProjectConfig {

    private var tinyUrl: String? = null

    @JvmField
    val tinyHeadName: List<String>? = null

    @JvmField
    val tinyHeadValue: List<String>? = null

    /**
     * setting->tools
     */
    private var autoDetectImage: Boolean = true


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
}
