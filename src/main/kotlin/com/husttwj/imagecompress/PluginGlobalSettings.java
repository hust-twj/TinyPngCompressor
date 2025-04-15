package com.husttwj.imagecompress;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
import java.awt.Dimension;
import java.awt.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "ImageCompressPro",
        storages = {@Storage(value = "$APP_CONFIG$/tinypng-image-optimizer.xml")}
)
public class PluginGlobalSettings implements PersistentStateComponent<PluginGlobalSettings> {
    public String version;
    public String uuid;
    public String username;
    public String apiKey;
    public int dialogLocationX;
    public int dialogLocationY;
    public int dialogSizeWidth;
    public int dialogSizeHeight;
    public int dividerLocation = 200;

    @Nullable
    public PluginGlobalSettings getState() {
        return this;
    }

    public void loadState(@NotNull PluginGlobalSettings settings) {

        XmlSerializerUtil.copyBean(settings, this);
    }

    public static PluginGlobalSettings getInstance() {
        return (PluginGlobalSettings)ServiceManager.getService(PluginGlobalSettings.class);
    }

    public void setDialogSize(Dimension dimension) {
        this.dialogSizeWidth = dimension.width;
        this.dialogSizeHeight = dimension.height;
    }

    @Transient
    public Dimension getDialogSize() {
        return new Dimension(this.dialogSizeWidth, this.dialogSizeHeight);
    }

    public void setDialogLocation(Point location) {
        this.dialogLocationX = location.x;
        this.dialogLocationY = location.y;
    }

    @Transient
    public Point getDialogLocation() {
        return new Point(this.dialogLocationX, this.dialogLocationY);
    }
}

