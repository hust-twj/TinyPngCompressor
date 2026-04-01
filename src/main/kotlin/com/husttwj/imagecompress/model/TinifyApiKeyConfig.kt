package com.husttwj.imagecompress.model

data class TinifyApiKeyConfig(
    var apiKey: String = "",
    var active: Boolean = false,
    var lastValidatedAt: Long = 0L
)
