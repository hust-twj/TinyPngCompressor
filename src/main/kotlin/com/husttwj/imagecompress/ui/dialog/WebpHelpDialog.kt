package com.husttwj.imagecompress.ui.dialog

import com.husttwj.imagecompress.ui.settings.TinyPngBundle
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import java.awt.Window
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextArea
import javax.swing.ScrollPaneConstants
import javax.swing.SwingConstants

class WebpHelpDialog(owner: Window?) : JDialog(owner, TinyPngBundle.message("dialog.webpHelp.title"), ModalityType.APPLICATION_MODAL) {

    init {
        configureUI()
    }

    private fun configureUI() {
        val title = TinyPngBundle.message("dialog.webpHelp.title")
        val content = TinyPngBundle.message("dialog.webpHelp.content")

        val panel = JPanel(BorderLayout(0, 12))
        panel.border = JBUI.Borders.empty(14, 16, 12, 16)

        val titleLabel = JLabel(title, SwingConstants.CENTER)
        titleLabel.font = titleLabel.font.deriveFont(Font.BOLD, 16f)
        panel.add(titleLabel, BorderLayout.NORTH)

        val textArea = JTextArea(content).apply {
            lineWrap = true
            wrapStyleWord = true
            isEditable = false
            isOpaque = false
            isFocusable = false
            border = JBUI.Borders.empty(2, 2, 2, 2)
        }

        val scrollPane = JBScrollPane(textArea).apply {
            border = null
            horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        }
        panel.add(scrollPane, BorderLayout.CENTER)

        val okButton = JButton("OK").apply {
            addActionListener { dispose() }
        }
        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT, 0, 0)).apply {
            add(okButton)
        }
        panel.add(buttonPanel, BorderLayout.SOUTH)

        contentPane = panel
        minimumSize = Dimension(760, 470)
        size = Dimension(760, 470)
        setLocationRelativeTo(owner)
        rootPane.defaultButton = okButton
    }
}
