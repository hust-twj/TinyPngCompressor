package com.husttwj.imagecompress.ui.dialog;


import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.CheckboxTree;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.IconUtil;
import javax.swing.JTree;


public class FileCellRenderer extends CheckboxTree.CheckboxTreeCellRenderer {
    private final Project myProject;

    public FileCellRenderer(Project project) {
        this.myProject = project;
    }

    public void customizeRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        FileTreeNode node = (FileTreeNode) value;
        VirtualFile file = node.getVirtualFile();
        if (file == null) {
            return;
        }
        ColoredTreeCellRenderer renderer = getTextRenderer();
        renderer.setIcon(IconUtil.getIcon(file, 1, this.myProject));
        renderer.append(file.getName());
        if (node.getImageBuffer() != null) {
            long optimized = 100 - ((node.getImageBuffer().length * 100) / file.getLength());
            renderer.append(String.format("  %d%%", Long.valueOf(optimized)), SimpleTextAttributes.DARK_TEXT, 16, 4);
        }
    }
}

