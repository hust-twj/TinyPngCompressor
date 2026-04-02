package com.husttwj.imagecompress.ui.dialog

import com.husttwj.imagecompress.ui.settings.TinyPngBundle
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import java.awt.Window
import java.util.Locale
import java.util.ResourceBundle
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextArea
import javax.swing.ScrollPaneConstants
import javax.swing.SwingConstants

class WebpHelpDialog(owner: Window?) : JDialog(owner, "", ModalityType.APPLICATION_MODAL) {

    private enum class HelpLanguage { EN, ZH }

    private val noFallbackControl = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT)
    private val englishBundle = ResourceBundle.getBundle(
        "messages.TinyPngBundle",
        Locale.ROOT,
        WebpHelpDialog::class.java.classLoader,
        noFallbackControl
    )
    private val chineseBundle = ResourceBundle.getBundle(
        "messages.TinyPngBundle",
        Locale.SIMPLIFIED_CHINESE,
        WebpHelpDialog::class.java.classLoader,
        noFallbackControl
    )

    private var language: HelpLanguage = HelpLanguage.EN

    private val titleLabel = JLabel("", SwingConstants.CENTER)
    private val textArea = JTextArea()
    private val languageButton = JButton()

    init {
        configureUI()
        refreshContent()
    }

    private fun configureUI() {
        val panel = JPanel(BorderLayout(0, 12))
        panel.border = JBUI.Borders.empty(14, 16, 12, 16)

        titleLabel.font = titleLabel.font.deriveFont(Font.BOLD, 16f)
        val header = JPanel(BorderLayout())
        header.add(titleLabel, BorderLayout.CENTER)
        header.add(createLanguageToggleButton(), BorderLayout.EAST)
        panel.add(header, BorderLayout.NORTH)

        textArea.lineWrap = true
        textArea.wrapStyleWord = true
        textArea.isEditable = false
        textArea.isOpaque = false
        textArea.isFocusable = false
        textArea.border = JBUI.Borders.empty(2, 2, 2, 2)

        val scrollPane = JBScrollPane(textArea)
        scrollPane.border = null
        scrollPane.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        panel.add(scrollPane, BorderLayout.CENTER)

        val okButton = JButton("OK")
        okButton.addActionListener { dispose() }
        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT, 0, 0))
        buttonPanel.add(okButton)
        panel.add(buttonPanel, BorderLayout.SOUTH)

        contentPane = panel
        minimumSize = Dimension(760, 470)
        size = Dimension(760, 470)
        setLocationRelativeTo(owner)
        rootPane.defaultButton = okButton
    }

    private fun createLanguageToggleButton(): JButton {
        languageButton.icon = IconLoader.getIcon("/icons/translate.svg", WebpHelpDialog::class.java)
        languageButton.isFocusable = false
        languageButton.border = JBUI.Borders.empty(4)
        languageButton.isContentAreaFilled = true
        languageButton.isBorderPainted = false
        languageButton.isOpaque = false
        languageButton.preferredSize = Dimension(28, 28)
        languageButton.minimumSize = Dimension(28, 28)
        languageButton.maximumSize = Dimension(28, 28)
        languageButton.addActionListener {
            language = if (language == HelpLanguage.EN) HelpLanguage.ZH else HelpLanguage.EN
            refreshContent()
        }
        return languageButton
    }

    private fun refreshContent() {
        val bundle = if (language == HelpLanguage.EN) englishBundle else chineseBundle
        title = bundle.getString("dialog.webpHelp.title")
        titleLabel.text = bundle.getString("dialog.webpHelp.title")
        textArea.text = bundle.getString("dialog.webpHelp.content")
        textArea.caretPosition = 0
        languageButton.toolTipText = if (language == HelpLanguage.EN) {
            TinyPngBundle.message("dialog.webpHelp.switchToChinese")
        } else {
            TinyPngBundle.message("dialog.webpHelp.switchToEnglish")
        }
    }
}
