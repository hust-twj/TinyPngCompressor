package com.husttwj.imagecompress.util;


public class StringUtils {

    public static String getFileSize(long fileSize) {
        return getFileSize(fileSize, false);
    }

    public static String getFileSize(long fileSize, boolean withOriginSize) {
        String unit = "B";
        float size = 0;
        if (fileSize < 1000) {
            size = fileSize;
        } else if (fileSize < 1000L * 1000L) {
            unit = "KB";
            size = (1.0f * fileSize / (1000L));
        } else if (fileSize < 1000L * 1000L * 1000L) {
            unit = "M";
            size = (1.0f * fileSize / (1000L * 1000L));
        } else if (fileSize < 1000L * 1000L * 1000L * 1000L) {
            unit = "G";
            size = (1.0f * fileSize / (1000L * 1000L * 1000L));
        } else {
            unit = "T";
            size = (1.0f * fileSize / (1000L * 1000L * 1000L * 1000L));
        }
        return String.format("%.1f", size) + unit + (withOriginSize ? (" (" + fileSize + "B)") : "");
    }

}
