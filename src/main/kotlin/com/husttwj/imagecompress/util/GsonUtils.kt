package com.husttwj.imagecompress.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object GsonUtils {

    @JvmField
    var sGson: Gson = GsonBuilder()
        .serializeSpecialFloatingPointValues()
        .create()
}
