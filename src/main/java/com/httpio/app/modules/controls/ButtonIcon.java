package com.httpio.app.modules.controls;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ButtonIcon extends Button {

    private final String ICON_ADD = "Add.png";

    public ButtonIcon(String icon) {
        super();

        ImageView iconImage = new ImageView(new Image(getClass().getResourceAsStream("/icons/" + icon)));

        iconImage.setFitHeight(15);
        iconImage.setFitWidth(15);

        this.setGraphic(iconImage);//setting icon to button
    }
}
