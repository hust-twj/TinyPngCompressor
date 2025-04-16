package com.husttwj.imagecompress.action;


import com.husttwj.imagecompress.Icons;
import com.husttwj.imagecompress.PluginGlobalSettings;
import com.husttwj.imagecompress.ui.dialog.TinyImageDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.util.ArrayUtil;

import com.tinify.Tinify;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;


/**
 * 右键压缩，出现图片压缩弹窗
 */
public class CompressAction extends AnAction {
    private static final String[] supportedExtensions = {"png", "jpg", "jpeg"};

    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        VirtualFile[] roots = (VirtualFile[]) PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(e.getDataContext());
        JFrame frame = WindowManager.getInstance().getFrame(project);
        if (roots == null || frame == null) {
            return;
        }
        if (StringUtil.isEmptyOrSpaces(Tinify.key())) {
            PluginGlobalSettings settings = PluginGlobalSettings.getInstance();
            if (StringUtil.isEmptyOrSpaces(settings.apiKey)) {
                settings.apiKey = Messages.showInputDialog(project, "What's your TinyPNG API Key?", "API Key", Messages.getQuestionIcon());
            }
            if (StringUtil.isEmptyOrSpaces(settings.apiKey)) {
                return;
            } else {
                Tinify.setKey(settings.apiKey);
            }
        }
        List<VirtualFile> list = getSupportedFileList(roots);
        TinyImageDialog dialog = new TinyImageDialog(project, List.of(roots), list, false, null, null);
        dialog.setDialogSize(frame);
        dialog.setVisible(true);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        List<VirtualFile> list = getSupportedFileList((VirtualFile[]) PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(e.getDataContext()));
        presentation.setIcon(Icons.ACTION);
        presentation.setEnabled(!list.isEmpty());
        super.update(e);
    }

    private List<VirtualFile> getSupportedFileList(VirtualFile[] files) {
        List<VirtualFile> result = new LinkedList<>();
        if (files == null) {
            return result;
        }
        for (VirtualFile file : files) {
            if (file.isDirectory()) {
                result.addAll(getSupportedFileList(file.getChildren()));
            } else {
                String extension = file.getExtension();
                if (extension != null && ArrayUtil.contains(extension.toLowerCase(), supportedExtensions)) {
                    result.add(file);
                }
            }
        }
        return result;
    }
}