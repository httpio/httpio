package com.httpio.app.util;

import com.httpio.app.modules.views.ProjectRequestsTree;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.input.DragEvent;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class NodeHelper {

    private Node node;

    public NodeHelper(Node node) {
        this.node = node;
    }

    /**
     * Styles
     */
    public void setStyle(String name, String value) {

    }

    public void setStyles(HashMap<String, String> styles) {
        setStyles(styles, false);
    }

    public void setStyles(HashMap<String, String> styles, boolean overwrite) {
        if (overwrite) {
            node.setStyle(implodeStyle(styles));
        } else {
            HashMap<String, String> exploded = explodeStyle(node.getStyle());
        }
    }

    public void removeStyle(String name) {
        HashMap<String, String> exploded = explodeStyle(node.getStyle());

        if (exploded.containsKey(name)) {
            exploded.remove(name);
        }

        setStyles(exploded, true);
    }

    private static HashMap<String, String> explodeStyle(String style) {
        HashMap<String, String> exploded = new HashMap<>();

        if (!style.equals("")) {
            for(String e: style.split(";")) {
                String[] a = e.split(":");

                exploded.put(a[0].trim(), a[1].trim());
            }
        }

        return exploded;
    }

    private static String implodeStyle(HashMap<String, String> styles) {
        StringBuilder imploded = new StringBuilder();

        for(Map.Entry<String, String> entry: styles.entrySet()) {
            imploded.append(entry.getKey() + ":" + entry.getValue());
        }

        return imploded.toString();
    }

    /**
     * Classes
     */
    public void addClass(String name) {
        if (node.getStyleClass().contains(name)) {
            return;
        }

        node.getStyleClass().add(name);
    }

    public void removeClass(String name) {
        node.getStyleClass().remove(name);

        return;
    }

    public int getZoneY(DragEvent event) {
        Bounds bounds = node.localToScene(node.getBoundsInLocal());

        double height = bounds.getMaxY() - bounds.getMinY();
        double y = event.getSceneY();
        double q = height / 4;

        if (y < bounds.getMinY() + q) {
            return 0;
        } else if (y > bounds.getMinY() + (3 * q)) {
            return 2;
        } else {
            return 1;
        }
    }
}
