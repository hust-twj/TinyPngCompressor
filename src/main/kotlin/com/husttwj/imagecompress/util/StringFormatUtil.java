package com.husttwj.imagecompress.util;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


public class StringFormatUtil {
    public StringFormatUtil() {
    }

    public static String humanReadableByteCount(long bytes) {
        return humanReadableByteCount(bytes, false);
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < (long)(unit * 10)) {
            return String.format("%,d B", bytes);
        } else {
            int exp = (int)(Math.log((double)bytes) / Math.log((double)unit));
            String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
            return String.format("%.1f %sB", (double)bytes / Math.pow((double)unit, (double)exp), pre);
        }
    }
}

