package com.httpio.app.modules.views;

import com.google.inject.Injector;
import com.httpio.app.modules.ControllerInterface;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.IOException;

/**
 * Base implementation of View.
 */
public class View<T extends ControllerInterface> {
    Parent view;
    FXMLLoader loader;

    public View(Injector injector) {
        loader = new FXMLLoader(this.getClass().getResource("main.fxml"));

        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> type) {
                return injector.getInstance(type);
            }
        });
    }

    public Parent getView() {
        return view;
    }

    public void showAndWait() {
        Scene scene = new Scene(view, 300, 200);
        Stage stage = new Stage();

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public T getController() {
        return loader.getController();
    }

    public void load() throws IOException {
        view = loader.load();

        loader.<T>getController().prepare();

        initialize(loader.getController());
    }

    protected void initialize(T controller) {}

    public void refresh() {
        getController().refresh();
    }
}
