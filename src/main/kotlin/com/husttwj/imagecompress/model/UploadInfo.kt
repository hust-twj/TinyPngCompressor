package com.husttwj.imagecompress.model

import com.husttwj.imagecompress.model.InputInfo
import com.husttwj.imagecompress.model.OutputInfo

class UploadInfo {

    var input: InputInfo? = null

    var output: OutputInfo? = null

    override fun toString(): String {
        return "UploadBean{" +
            "input=" + input +
            ", output=" + output +
            '}'
    }
}