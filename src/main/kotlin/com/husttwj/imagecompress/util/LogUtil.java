package com.husttwj.imagecompress.util;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {

    public static final boolean DEBUG = false;

    public static SimpleDateFormat sSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void d(String msg) {
        d(msg, null);
        System.out.print(msg);
    }

    public static void d(String msg, Throwable t) {
        logWhenDebug(msg, t, false);
        ThreadUtils.submitLog(() -> writeMsgToFile(msg, t));
    }

    public static void e(String msg, Throwable t) {
        logWhenDebug(msg, t, true);
        ThreadUtils.submitLog(() -> {
            writeMsgToFile(msg, t);
        });
    }

    public static String getTimeStamp() {
        return sSimpleDateFormat.format(new Date());
    }

    private static void writeMsgToFile(String msg, Throwable t) {
        try {
            final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(FileUtils.sLogFilePath, true), FileUtils.CHARSET_NAME);
            writer.write(sSimpleDateFormat.format(new Date()) + ": " + msg);
            writer.write("\n");
            if (t != null) {
                t.printStackTrace(new PrintWriter(writer));
                writer.write("\n");
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void logWhenDebug(String msg, Throwable throwable, boolean error) {
        if (DEBUG) {
            if (error) {
                System.err.println(msg);
            } else {
                System.out.println(msg);
            }
            if (throwable != null) {
                throwable.printStackTrace();
            }
        }
    }

    public static void e(String msg) {
        e(msg, null);
    }

}
