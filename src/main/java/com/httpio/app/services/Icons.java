package com.httpio.app.services;

import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Icons {
    public final static String ICON_ADD = "p2/Add.png";
    public final static String ICON_RENAME = "p2/Rename Document.png";
    public static final String ICON_REMOVE = "p2/Remove.png";
    public static final String ICON_NEW_FILE = "p1/New file.png";
    public static final String ICON_OPEN = "p1/Open.png";
    public static final String ICON_SAVE = "p1/Save.png";
    public static final String ICON_SAVE_AS = "p1/Save as.png";
    public static final String ICON_CLOSE = "p1/Close.png";

    public void attachIcon(MenuItem item, String icon) {
        ImageView iconImage = new ImageView(new Image(getClass().getResourceAsStream("/icons/" + icon)));

        iconImage.setFitHeight(15);
        iconImage.setFitWidth(15);

        item.setGraphic(iconImage);
    }
}
