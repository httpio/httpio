package com.httpio.app.modules.controls;

import com.httpio.app.models.Request;
import com.httpio.app.modules.ListenersContainer;
import com.httpio.app.services.Http;
import com.httpio.app.services.Http.Method;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class RequestLabel extends VBox {
    private HBox hbox = new HBox();
    private Text text = new Text();
    private Badge method = new Badge();
    private Text url = new Text();
    private ListenersContainer listenersContainer = new ListenersContainer();

    private ObjectProperty<Request> request = new SimpleObjectProperty<>();

    /**
     * Constructors
     */
    public RequestLabel() {
        super();

        init();
    }

    private void init() {
        getChildren().add(text);
        getChildren().add(hbox);

        hbox.getChildren().add(method);
        hbox.getChildren().add(url);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setSpacing(10);

        hbox.getStyleClass().clear();
        hbox.getStyleClass().add("httpio-controls-request-label__method-url-wrapper");

        method.getStyleClass().clear();
        method.getStyleClass().add("httpio-controls-request-label__method");

        url.getStyleClass().clear();
        url.getStyleClass().add("httpio-controls-request-label__resource");

        request.addListener((observable, old, request) -> {
            // loadRequest(old, request);
            loadRequest(request);
        });
    }

    private void loadRequest(Request request) {
        if (this.request != null) {
            listenersContainer.detachAll();
        }

        this.request.setValue(request);

        if (this.request.getValue() == null) {
            return;
        }


        text.textProperty().bind(request.nameProperty());

        listenersContainer.attach(request.methodProperty(), new ChangeListener<Method>() {
            @Override
            public void changed(ObservableValue<? extends Method> observable, Method old, Method method) {
                reloadMethod();
            }
        });

        reloadMethod();

        // method.textProperty().bind(new StringBinding() {
        //     {
        //         super.bind(request.methodProperty());
        //     }

        //     @Override
        //     protected String computeValue() {
        //         Method method = request.getMethod();

        //         if (method == null) {
        //             return null;
        //         }

        //         return method.getId().toString();
        //     }
        // });

        url.textProperty().bind(request.urlProperty());
    }

    private void reloadMethod() {
        if (request == null) {
            return;
        }

        method.setText(request.getValue().getMethod().getId().toString());
        method.setColor(request.getValue().getMethod().getColor());
    }

    /**
     * Request
     */
    public Request getRequest() {
        return request.get();
    }

    public void setRequest(Request request) {
        this.request.set(request);
    }

    public ObjectProperty<Request> requestProperty() {
        return request;
    }
}
