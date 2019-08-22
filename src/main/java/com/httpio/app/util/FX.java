package com.httpio.app.util;

import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class FX {
    public static String toRGBCode(Color color ) {
        return String.format( "#%02X%02X%02X",
            (int)( color.getRed() * 255 ),
            (int)( color.getGreen() * 255 ),
            (int)( color.getBlue() * 255 ));
    }

    public static void setStyle(Node node, String attribute, String value) {

        HashMap<String, String> exploded = new HashMap<>();

        System.out.println(node.getStyle());

        if (!node.getStyle().equals("")) {
            for(String e: node.getStyle().split(";")) {
                String[] a = e.split(":");

                exploded.put(a[0], a[1]);
            }
        }

        exploded.put(attribute, value);

        StringBuilder css = new StringBuilder();

        for(Map.Entry<String, String> entry: exploded.entrySet()) {
            css.append(entry.getKey().trim()).append(":").append(entry.getValue().trim()).append(";");
        }

        node.setStyle(css.toString());
    }
}
