package com.husttwj.imagecompress.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.vfs.VirtualFile
import java.util.function.Predicate

abstract class BaseAction : AnAction() {

    companion object {

        val sSupportedImageType =  listOf("png", "webp", "jpg", "jpeg")

        @JvmField
        var sPredicate =
            Predicate<VirtualFile> { virtualFile ->
                if (virtualFile.extension == null) {
                    false
                } else {
                    !virtualFile.path.contains("build/intermediates/")
                            && sSupportedImageType.contains(virtualFile.extension!!.lowercase())
                            && !virtualFile.name.lowercase().endsWith(".9.png")
                            && !virtualFile.name.lowercase().endsWith(".9.webp")
                }
            }
    }
}