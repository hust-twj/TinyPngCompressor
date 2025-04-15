package com.husttwj.imagecompress.service;


import com.husttwj.imagecompress.PluginGlobalSettings;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;

import com.tinify.Tinify;
import java.io.IOException;

public class TinyPNG {
    private boolean imageProcessed = false;

    public TinyPNG() {
    }

    public static byte[] process(VirtualFile file) throws IOException {
        if (StringUtil.isEmptyOrSpaces(Tinify.key())) {
            PluginGlobalSettings settings = PluginGlobalSettings.getInstance();
            Tinify.setKey(settings.apiKey);
        }

        return Tinify.fromFile(file.getPath()).toBuffer();
    }

    public boolean isImageProcessed() {
        return this.imageProcessed;
    }

    public int getCompressionCount() {
        return Tinify.compressionCount();
    }
}

