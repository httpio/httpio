package com.httpio.app.services;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Windows {
    public <T extends Parent> Window<T> create(T view) {
        return new Window<T>(view);
    }

    public class Window<T extends Parent> {
        public final String SIZE_NORMAL = "Normal";
        public final String SIZE_SMALL = "Small";
        public final String SIZE_MEDIUM = "Medium";
        public final String SIZE_LARGET = "Large";

        T view;
        Scene scene;
        Stage stage;

        public Window(T view) {
            this.view = view;

            // scene = new Scene(view, 300, 200);
            scene = new Scene(view);
            stage = new Stage();

            setSize(SIZE_NORMAL);
        }

        public T getView() {
            return view;
        }

        public void setSize(String size) {

            double width;
            double height;

            if (size.equals(SIZE_SMALL)) {
                width = 100;
            } else if (size.equals(SIZE_MEDIUM)) {
                width = 700;
            } else if (size.equals(SIZE_NORMAL)) {
                width = 1024;
            } else if (size.equals(SIZE_LARGET)) {
                width = 2000;
            }  else {
                width = 2000;
            }

            stage.setWidth(width);
            stage.setHeight(width * 0.8);
        }

        public void showAndWait() {
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        }

        public void close() {
            stage.close();
        }

        public void setTitle(String title) {
            stage.setTitle(title);
        }
    }
}
