package com.httpio.app.views;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class CreateFromRAW extends VBox {
    @FXML
    private Text messageField;

    @FXML
    private TextArea textField;

    @FXML
    private Button createButton;

    @FXML
    private Button cancelButton;

    public CreateFromRAW() {
        super();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateFromRAW.fxml"));

        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StringProperty messageTextProperty() {
        return messageField.textProperty();
    }

    public String getMessage() {
        return messageField.getText();
    }

    public String getText() {
        return textField.getText();
    }

    public StringProperty textProperty() {
        return textField.textProperty();
    }

    public Button getCreateButton() {
        return createButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }
}
