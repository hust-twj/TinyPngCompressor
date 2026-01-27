package com.husttwj.imagecompress.ui.settings

import com.intellij.DynamicBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.PropertyKey


private const val BUNDLE = "messages.TinyPngBundle"

object TinyPngBundle : DynamicBundle(BUNDLE) {

    @JvmStatic
    @Nls
    fun message(
        @PropertyKey(resourceBundle = BUNDLE) key: String,
        vararg params: Any
    ): String = getMessage(key, *params)
}