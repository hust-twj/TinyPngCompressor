package com.husttwj.imagecompress.util

import com.husttwj.imagecompress.listener.OnClickListener
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import javax.swing.AbstractAction
import javax.swing.JComponent
import javax.swing.KeyStroke


object JComponentUtils {

    @JvmStatic
    fun setSize(jComponent: JComponent, width: Int, height: Int) {
        jComponent.minimumSize = Dimension(width, height)
        jComponent.maximumSize = Dimension(width, height)
        jComponent.preferredSize = Dimension(width, height)
        jComponent.size = Dimension(width, height)
    }

    @JvmStatic
    fun supportCommandW(component: JComponent, onClickListener: OnClickListener?) {
        val closeKey = KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())
        component.inputMap.put(closeKey, "closeWindow")
        component.actionMap.put("closeWindow", object : AbstractAction("Close Window") {
            override fun actionPerformed(e: ActionEvent?) {
                onClickListener?.onClick()
            }
        })
    }
}
