package com.husttwj.imagecompress.ui;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import com.husttwj.imagecompress.PluginGlobalSettings;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications.Bus;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.text.StringUtil;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class PluginApplicationComponent implements ApplicationComponent {
    public static final PluginId PLUGIN_ID = PluginId.getId("com.husttwj.ImageCompress");

    public void initComponent() {
        IdeaPluginDescriptor plugin = PluginManager.getPlugin(PLUGIN_ID);
        if (plugin != null) {
            PluginGlobalSettings settings = PluginGlobalSettings.getInstance();
            if (StringUtil.isEmpty(settings.uuid)) {
                settings.uuid = UUID.randomUUID().toString();
            }

            if (StringUtil.isEmpty(settings.username)) {
                settings.username = settings.uuid;
            }

            if (!plugin.getVersion().equals(settings.version)) {
                settings.version = plugin.getVersion();
                String popupTitle = plugin.getName() + " v" + plugin.getVersion();
                NotificationGroup group = new NotificationGroup(plugin.getName(), NotificationDisplayType.STICKY_BALLOON, true);
                Notification notification = group.createNotification(popupTitle, plugin.getChangeNotes(), NotificationType.INFORMATION, NotificationListener.URL_OPENING_LISTENER);
                Bus.notify(notification);
            }
        }
    }

    public void disposeComponent() {
    }

    @NotNull
    public String getComponentName() {
//        if ("TinyPNG PluginApplicationComponent" == null) {
//            $$$reportNull$$$0(0);
//        }

        return "TinyPNG PluginApplicationComponent";
    }

    public static PluginApplicationComponent getInstance() {
        return (PluginApplicationComponent)ApplicationManager.getApplication().getComponent(PluginApplicationComponent.class);
    }
}

