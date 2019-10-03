package com.httpio.app.util;

import javafx.scene.control.TreeItem;
import org.junit.Test;

import static org.junit.Assert.*;

public class TreeItemHelperTest {
    @Test
    public void hasParent() {
        TreeItem<String> root = createTree();

        TreeItem<String> aa = root.getChildren().get(0).getChildren().get(0);
        TreeItem<String> ab = root.getChildren().get(0).getChildren().get(1);

        TreeItemHelper<String> treeItemHelper = new TreeItemHelper<>(aa);

        assertEquals(true, treeItemHelper.hasParent(root));
        assertEquals(false, treeItemHelper.hasParent(ab));
    }

    private static TreeItem<String> createTree() {
        TreeItem<String> root = new TreeItem<>();

        TreeItem<String> a = new TreeItem<>();
        TreeItem<String> b = new TreeItem<>();
        TreeItem<String> c = new TreeItem<>();

        root.getChildren().addAll(a, b, c);

        TreeItem<String> aa = new TreeItem<>();
        TreeItem<String> ab = new TreeItem<>();
        TreeItem<String> ac = new TreeItem<>();

        a.getChildren().addAll(aa, ab, ac);

        return root;
    }
}