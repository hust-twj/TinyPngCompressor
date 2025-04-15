package com.husttwj.imagecompress.ui.settings;


import com.husttwj.imagecompress.PluginGlobalSettings;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.text.StringUtil;

import com.tinify.Tinify;

import javax.swing.*;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Nls.Capitalization;

import java.awt.*;

public class Settings implements Configurable {
    private JPanel mainPanel;
    private JTextField apiKey;
    private JLabel usage;

    public Settings() {

        // 初始化组件
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(3, 1, 5, 5));

        JLabel apiKeyLabel = new JLabel("TinyPNG API Key:");
        apiKey = new JTextField(30);

        usage = new JLabel("Usage this month: -");

        contentPanel.add(apiKeyLabel);
        contentPanel.add(apiKey);
        contentPanel.add(usage);

        mainPanel.add(contentPanel, BorderLayout.NORTH);
    }

    @Nls(
       capitalization = Capitalization.Title
    )
    public String getDisplayName() {
        return "Compress Image";
    }

    public JComponent getPreferredFocusedComponent() {
        return this.apiKey;
    }

    @Nullable
    public JComponent createComponent() {
        PluginGlobalSettings settings = PluginGlobalSettings.getInstance();
        if (!StringUtil.isEmptyOrSpaces(settings.apiKey)) {
            Tinify.setKey(settings.apiKey);
            this.updateUsageCount();
        }

        return this.mainPanel;
    }

    public boolean isModified() {
        PluginGlobalSettings settings = PluginGlobalSettings.getInstance();
        if (StringUtil.isEmptyOrSpaces(settings.apiKey) && StringUtil.isEmptyOrSpaces(this.apiKey.getText())) {
            return false;
        } else {
            return !this.apiKey.getText().equals(settings.apiKey);
        }
    }

    public void apply() {
        PluginGlobalSettings settings = PluginGlobalSettings.getInstance();
        settings.apiKey = this.apiKey.getText();
        if (!StringUtil.isEmptyOrSpaces(settings.apiKey)) {
            this.updateUsageCount();
        }
    }

    public void reset() {
        PluginGlobalSettings settings = PluginGlobalSettings.getInstance();
        this.apiKey.setText(settings.apiKey);
    }

    private void updateUsageCount() {
        this.usage.setText("Usage this month: " + Tinify.compressionCount());
    }
}

