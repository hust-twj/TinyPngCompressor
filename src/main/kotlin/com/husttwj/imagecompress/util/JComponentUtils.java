package com.husttwj.imagecompress.util;


import com.husttwj.imagecompress.listener.OnClickListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class JComponentUtils {

    public static void setSize(JComponent jComponent, int width, int height) {
        jComponent.setMinimumSize(new Dimension(width, height));
        jComponent.setMaximumSize(new Dimension(width, height));
        jComponent.setPreferredSize(new Dimension(width, height));
        jComponent.setSize(new Dimension(width, height));
    }

    public static void supportCommandW(JComponent component, OnClickListener onClickListener) {
        KeyStroke closeKey = KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        component.getInputMap().put(closeKey, "closeWindow");
        component.getActionMap().put("closeWindow", new AbstractAction("Close Window") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onClickListener != null) {
                    onClickListener.onClick();
                }
            }
        });
    }
}
