package com.httpio.app.util;

import javafx.scene.control.TreeItem;

public class TreeItemHelper<T> {
    TreeItem<T> treeItem;

    public TreeItemHelper(TreeItem<T> treeItem) {
        this.treeItem = treeItem;
    }

    /**
     * Check if given element is parent of item.
     */
    public Boolean hasParent(TreeItem<T> item) {
        TreeItem<T> parent = treeItem.getParent();

        while(parent != null) {
            if (parent == item) {
                return true;
            } else {
                parent = parent.getParent();
            }
        }

        return false;
    }
}
