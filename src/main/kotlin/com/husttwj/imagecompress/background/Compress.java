package com.husttwj.imagecompress.background;


import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.util.ProgressIndicatorBase;
import com.intellij.openapi.progress.util.ProgressIndicatorUtils;
import com.intellij.openapi.progress.util.ReadTask;

public class Compress {

    public Compress() {
    }

    public void start() {
        ProgressIndicator progressIndicator = new ProgressIndicatorBase();
        ProgressIndicatorUtils.scheduleWithWriteActionPriority(progressIndicator, new CompressTask());
    }


    class CompressTask extends ReadTask {


        public void onCanceled(ProgressIndicator indicator) {

            indicator.stop();
        }
    }

}
