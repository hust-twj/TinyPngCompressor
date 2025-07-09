package com.husttwj.imagecompress.util;


import com.husttwj.imagecompress.model.ProjectConfig;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;

public class FileUtils {

    public static final String CHARSET_NAME = "UTF-8";

    public static final String LOG_FILE_NAME = "tinypng_compressor_log.txt";

    public static final String CONFIG_INFO_FILE_NAME = "tinypng_compressor_config.txt";

    public static String sUserDesktopPath;

    public static String sMainDirPath;

    public static String sTmpFileDirPath;

    public static String sHistoryFileDirPath;

    public static String sImageFileDirPath;

    public static String sPluginInstallDir = null;

    public static String sPluginDir = null;

    public static String sLogFilePath;

    public static void init() {
        if (sLogFilePath != null) {
            return;
        }
        initDir();
        initLogFile();
        initPluginInstallPath();
        OSHelper.getInstance().init();
        LogUtil.d("FileUtils init success");
    }

    private static ProjectConfig sProjectConfig;

    public static ProjectConfig getConfig() {
        if (sProjectConfig != null) {
            return sProjectConfig;
        }
        final File file = new File(sMainDirPath, CONFIG_INFO_FILE_NAME);
        if (!file.exists()) {
            return new ProjectConfig();
        }
        try {
            final String fileContent = FileUtils.getFileContent(file);
            sProjectConfig = GsonUtils.sGson.fromJson(fileContent, ProjectConfig.class);
            if (sProjectConfig == null) {
                sProjectConfig = new ProjectConfig();
            }
            return sProjectConfig;
        } catch (Throwable t) {
            return new ProjectConfig();
        }
    }

    public static List<VirtualFile> getMatchFileList(VirtualFile[] files, Predicate<VirtualFile> predicate, boolean breakWhenFoundOne) {
        List<VirtualFile> result = new LinkedList<>();
        if (files == null) {
            return result;
        }
        for (VirtualFile file : files) {
            if (file.isDirectory()) {
                if ("build".equals(file.getName())) {
                    continue;
                }
                result.addAll(getMatchFileList(file.getChildren(), predicate, breakWhenFoundOne));
                if (breakWhenFoundOne && !result.isEmpty()) {
                    break;
                }
            } else {
                if (predicate.test(file)) {
                    result.add(file);
                    if (breakWhenFoundOne) {
                        break;
                    }
                }
            }
        }
        return result;
    }

    private static void initPluginInstallPath() {
        File pluginPath = PluginManager.getPlugin(PluginId.getId("com.husttwj.TinyPngCompressor")).getPath();
        if (pluginPath.exists()) {
            sPluginInstallDir = pluginPath.getParent();
            sPluginDir = pluginPath.getAbsolutePath();
        }
    }

    private static void initDir() {
        final String userHomePath = System.getProperty("user.home");
        sUserDesktopPath = OSHelper.getInstance().getUserDesktopFilePath();
        initMainFileDir(userHomePath);
        initTmpFileDir();
        initHistoryFileDir();
        initImageFileDir();
        deleteChildFile(sImageFileDirPath);
        deleteChildFile(sTmpFileDirPath);
    }

    private static void initMainFileDir(String userHomePath) {
        final File file = new File(userHomePath, ".tinypng_compressor_main");
        sMainDirPath = file.getPath();
        if (!file.exists()) {
            final boolean mkdirs = file.mkdirs();
            if (!mkdirs && sUserDesktopPath.equals(userHomePath)) {
                initMainFileDir(sUserDesktopPath);
            }
        } else if (!file.isDirectory()) {
            file.delete();
            file.mkdirs();
        }
    }

    private static void initImageFileDir() {
        final File file = new File(sMainDirPath, "image");
        sImageFileDirPath = file.getPath();
        if (!file.exists()) {
            file.mkdirs();
        } else if (!file.isDirectory()) {
            file.delete();
            file.mkdirs();
        }
    }

    private static void initHistoryFileDir() {
        final File file = new File(sMainDirPath, "historyFile");
        sHistoryFileDirPath = file.getPath();
        if (!file.exists()) {
            file.mkdirs();
        } else if (!file.isDirectory()) {
            file.delete();
            file.mkdirs();
        }
    }

    private static void initTmpFileDir() {
        final File file = new File(sMainDirPath, "tempFile");
        sTmpFileDirPath = file.getPath();
        if (!file.exists()) {
            file.mkdirs();
        } else if (!file.isDirectory()) {
            file.delete();
            file.mkdirs();
        }
    }

    private static void initLogFile() {
        File logFile = new File(sMainDirPath, LOG_FILE_NAME);
        sLogFilePath = logFile.getAbsolutePath();
        if (!logFile.exists()) {
            createLogFile();
        } else {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sLogFilePath), FileUtils.CHARSET_NAME));
                final String timeStart = reader.readLine().trim();
                reader.close();
                final Long lastUpdateTime = Long.valueOf(timeStart);
                if (System.currentTimeMillis() - lastUpdateTime > 3 * 60L * 60 * 24 * 1000) {
                    createLogFile();
                    FileUtils.deleteChildFile(FileUtils.sTmpFileDirPath);
                }
            } catch (Exception e) {
                createLogFile();
            }
        }
    }

    private static void createLogFile() {
        final File file = new File(sLogFilePath);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(sLogFilePath));
            bufferedWriter.write(System.currentTimeMillis() + "\n");
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getFileContent(File file) {
        try {
            final byte[] fileContentBytes = getFileContentBytes(file);
            if (fileContentBytes != null) {
                return new String(fileContentBytes, CHARSET_NAME);
            }
        } catch (Exception e) {
            LogUtil.e("read file error: " + file.getAbsolutePath(), e);
        }
        return "";
    }

    public static byte[] getFileContentBytes(File file) {
        if (!file.exists() || file.isDirectory()) {
            return null;
        }
        byte[] fileBytes = new byte[(int) file.length()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(fileBytes);
            in.close();
            return fileBytes;
        } catch (Exception e) {
            LogUtil.e("read file error: " + file.getAbsolutePath(), e);
        }
        return null;
    }

    public static void deleteChildFile(String filePath) {
        deleteChildFile(new File(filePath));
    }

    public static void deleteChildFile(File file) {
        if (file == null || !file.exists() || !file.isDirectory()) {
            return;
        }
        final File[] files = file.listFiles();
        for (File f : files) {
            deleteFile(f);
        }
    }

    public static void deleteFile(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            final File[] files = file.listFiles();
            for (File f : files) {
                deleteFile(f);
            }
            file.delete();
        } else {
            file.delete();
        }
    }

    @NotNull
    public static ByteArrayOutputStream readByteArrayOutputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream;
        try {
            byte[] buffer = new byte[4096];
            int readLength = -1;
            byteArrayOutputStream = new ByteArrayOutputStream();
            while ((readLength = inputStream.read(buffer)) > 0) {
                byteArrayOutputStream.write(buffer, 0, readLength);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                //关闭流
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return byteArrayOutputStream;
    }

}