package com.husttwj.imagecompress.listener;


import com.husttwj.imagecompress.util.FileUtils;


public class CompressApplicationInitializedListener implements com.intellij.ide.ApplicationInitializedListener {

    @Override
    public void componentsInitialized() {
        FileUtils.init();

    }

}
