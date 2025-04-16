package com.husttwj.imagecompress.util;

import org.jetbrains.annotations.Nullable;


public class StringUtils {

    @Nullable
    public static String grepLine(String originStr, String key) {
        if (originStr == null) {
            return null;
        }
        final String[] split = originStr.split("\n");
        for (String line : split) {
            if (line != null && line.contains(key)) {
                return line;
            }
        }
        return null;
    }

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
