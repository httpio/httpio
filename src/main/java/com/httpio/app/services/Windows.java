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
        T view;
        Scene scene;
        Stage stage;

        public Window(T view) {
            this.view = view;
        }

        public T getView() {
            return view;
        }

        public void showAndWait() {
            scene = new Scene(view, 300, 200);
            stage = new Stage();

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        }

        public void close() {
            stage.close();
        }
    }
}
