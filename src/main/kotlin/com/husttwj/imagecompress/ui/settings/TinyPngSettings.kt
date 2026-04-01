package com.husttwj.imagecompress.ui.settings

import com.husttwj.imagecompress.listener.VirtualFileListenerService
import com.husttwj.imagecompress.model.ProjectConfig
import com.husttwj.imagecompress.model.TinifyApiKeyConfig
import com.husttwj.imagecompress.util.FileUtils
import com.husttwj.imagecompress.util.TinyPngV2
import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.ListSelectionModel
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.AbstractTableModel

class TinyPngSettings(private val project: Project) : Configurable {

    private lateinit var autoDetectImageCheckBox: JBCheckBox
    private lateinit var apiKeyTableModel: TinifyApiKeyTableModel
    private lateinit var apiKeyTable: JBTable
    private lateinit var statusHintLabel: JBLabel

    private var config: ProjectConfig? = null

    override fun getDisplayName(): String = "TinyPngCompressor"

    override fun getPreferredFocusedComponent(): JComponent? = autoDetectImageCheckBox

    override fun createComponent(): JComponent {
        config = FileUtils.getConfig()
        autoDetectImageCheckBox = JBCheckBox(
            TinyPngBundle.message("settings.autoDetectImageOption"),
            config?.isAutoDetectImage() ?: true
        )
        apiKeyTableModel = TinifyApiKeyTableModel(config?.getTinifyApiKeys()?.map { it.copyItem() } ?: emptyList())
        apiKeyTable = JBTable(apiKeyTableModel)
        apiKeyTable.selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
        apiKeyTable.fillsViewportHeight = true
        apiKeyTable.columnModel.getColumn(1).cellRenderer = StatusCellRenderer()

        val addButton = createIconButton(AllIcons.General.Add, TinyPngBundle.message("settings.tinify.add"))
        addButton.addActionListener {
            stopEditing()
            apiKeyTableModel.addRow(TinifyApiKeyConfig())
            val row = apiKeyTableModel.rowCount - 1
            if (row >= 0) {
                apiKeyTable.setRowSelectionInterval(row, row)
            }
        }

        val deleteButton = createIconButton(AllIcons.General.Remove, TinyPngBundle.message("settings.tinify.delete"))
        deleteButton.addActionListener {
            stopEditing()
            val selectedRow = apiKeyTable.selectedRow
            if (selectedRow >= 0) {
                apiKeyTableModel.removeRow(apiKeyTable.convertRowIndexToModel(selectedRow))
            }
        }

        val refreshButton = createIconButton(AllIcons.Actions.Refresh, TinyPngBundle.message("settings.tinify.refresh"))
        refreshButton.addActionListener {
            refreshStatusesInBackground()
        }

        statusHintLabel = JBLabel(TinyPngBundle.message("settings.tinify.hint"))

        val actionPanel = JPanel(FlowLayout(FlowLayout.LEFT, 4, 0))
        actionPanel.add(JBLabel(TinyPngBundle.message("settings.tinify.title")))
        actionPanel.add(addButton)
        actionPanel.add(deleteButton)
        actionPanel.add(refreshButton)

        val content = JPanel(BorderLayout(0, 8))
        content.add(autoDetectImageCheckBox, BorderLayout.NORTH)

        val apiKeyPanel = JPanel(BorderLayout(0, 8))
        apiKeyPanel.add(actionPanel, BorderLayout.NORTH)
        apiKeyPanel.add(JBScrollPane(apiKeyTable), BorderLayout.CENTER)
        apiKeyPanel.add(statusHintLabel, BorderLayout.SOUTH)

        content.add(apiKeyPanel, BorderLayout.CENTER)
        refreshStatusesInBackground()
        return content
    }

    override fun isModified(): Boolean {
        val currentConfig = config ?: return false
        if (autoDetectImageCheckBox.isSelected != currentConfig.isAutoDetectImage()) {
            return true
        }
        return normalize(apiKeyTableModel.items()) != normalize(currentConfig.getTinifyApiKeys())
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        stopEditing()
        val currentConfig = config ?: FileUtils.getConfig()
        currentConfig.setAutoDetectImage(autoDetectImageCheckBox.isSelected)
        currentConfig.setTinifyApiKeys(normalize(apiKeyTableModel.items()).toMutableList())
        TinyPngV2.refreshStatuses(currentConfig)
        apiKeyTableModel.replaceAll(currentConfig.getTinifyApiKeys().map { it.copyItem() })
        FileUtils.saveConfig(currentConfig)
        FileUtils.resetConfig()
        config = FileUtils.getConfig()

        val listenerService = project.getService(VirtualFileListenerService::class.java)
        if (autoDetectImageCheckBox.isSelected) {
            listenerService.startListening()
        } else {
            listenerService.stopListening()
        }
    }

    override fun reset() {
        stopEditing()
        config = FileUtils.getConfig()
        autoDetectImageCheckBox.isSelected = config?.isAutoDetectImage() ?: true
        apiKeyTableModel.replaceAll(config?.getTinifyApiKeys()?.map { it.copyItem() } ?: emptyList())
        refreshStatusesInBackground()
    }

    override fun disposeUIResources() {
        config = null
    }

    private fun refreshStatusesInBackground() {
        stopEditing()
        val snapshot = normalize(apiKeyTableModel.items()).toMutableList()
        statusHintLabel.text = TinyPngBundle.message("settings.tinify.refreshing")
        ApplicationManager.getApplication().executeOnPooledThread {
            val tempConfig = ProjectConfig().apply {
                setTinifyApiKeys(snapshot)
            }
            val result = runCatching {
                TinyPngV2.refreshStatuses(tempConfig)
                tempConfig.getTinifyApiKeys().map { it.copyItem() }
            }
            ApplicationManager.getApplication().invokeLater({
                result.onSuccess { refreshed ->
                    apiKeyTableModel.mergeStatuses(refreshed)
                    statusHintLabel.text = TinyPngBundle.message("settings.tinify.hint")
                }.onFailure { throwable ->
                    com.husttwj.imagecompress.util.LogUtil.d("Refresh Tinify status failed", throwable)
                    statusHintLabel.text = TinyPngBundle.message("settings.tinify.refreshFailed")
                }
            }, ModalityState.any())
        }
    }

    private fun stopEditing() {
        if (::apiKeyTable.isInitialized && apiKeyTable.isEditing) {
            apiKeyTable.cellEditor.stopCellEditing()
        }
    }

    private fun createIconButton(icon: javax.swing.Icon, toolTip: String): JButton {
        return JButton(icon).apply {
            this.toolTipText = toolTip
            isFocusable = false
            border = null
            setContentAreaFilled(false)
        }
    }

    private fun normalize(items: List<TinifyApiKeyConfig>): List<TinifyApiKeyConfig> {
        val result = linkedMapOf<String, TinifyApiKeyConfig>()
        for (item in items) {
            val apiKey = item.apiKey.trim()
            if (apiKey.isEmpty()) {
                continue
            }
            result[apiKey] = TinifyApiKeyConfig(apiKey, item.active, item.lastValidatedAt)
        }
        return result.values.toList()
    }

    private fun TinifyApiKeyConfig.copyItem(): TinifyApiKeyConfig {
        return TinifyApiKeyConfig(apiKey, active, lastValidatedAt)
    }
}

private class StatusCellRenderer : DefaultTableCellRenderer() {
    override fun setValue(value: Any?) {
        val status = value?.toString().orEmpty()
        text = status
        foreground = when (status) {
            "active" -> JBColor(0x2E7D32, 0x81C784)
            "inactive" -> JBColor(0xC62828, 0xEF9A9A)
            else -> JBColor.foreground()
        }
    }
}

private class TinifyApiKeyTableModel(
    initialItems: List<TinifyApiKeyConfig>
) : AbstractTableModel() {

    private val rows = initialItems.toMutableList()

    override fun getRowCount(): Int = rows.size

    override fun getColumnCount(): Int = 2

    override fun getColumnName(column: Int): String {
        return when (column) {
            0 -> TinyPngBundle.message("settings.tinify.apiKey")
            else -> TinyPngBundle.message("settings.tinify.status")
        }
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        val item = rows[rowIndex]
        return when (columnIndex) {
            0 -> item.apiKey
            else -> when {
                item.lastValidatedAt <= 0L -> ""
                item.active -> "active"
                else -> "inactive"
            }
        }
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = columnIndex == 0

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        if (columnIndex != 0) {
            return
        }
        rows[rowIndex].apiKey = aValue?.toString()?.trim().orEmpty()
        rows[rowIndex].active = false
        rows[rowIndex].lastValidatedAt = 0L
        fireTableRowsUpdated(rowIndex, rowIndex)
    }

    fun addRow(item: TinifyApiKeyConfig) {
        rows.add(item)
        val rowIndex = rows.lastIndex
        fireTableRowsInserted(rowIndex, rowIndex)
    }

    fun removeRow(index: Int) {
        rows.removeAt(index)
        fireTableRowsDeleted(index, index)
    }

    fun items(): List<TinifyApiKeyConfig> = rows.map { TinifyApiKeyConfig(it.apiKey, it.active, it.lastValidatedAt) }

    fun replaceAll(items: List<TinifyApiKeyConfig>) {
        rows.clear()
        rows.addAll(items)
        fireTableDataChanged()
    }

    fun mergeStatuses(items: List<TinifyApiKeyConfig>) {
        val statusMap = items.associateBy { it.apiKey.trim() }
        for (row in rows) {
            val updated = statusMap[row.apiKey.trim()] ?: continue
            row.active = updated.active
            row.lastValidatedAt = updated.lastValidatedAt
        }
        fireTableDataChanged()
    }
}
