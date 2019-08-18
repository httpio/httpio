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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class RequestLabel extends VBox {
    HBox hbox = new HBox();

    Text text = new Text();
    Text method = new Text();
    Text resource = new Text();

    ObjectProperty<Request> request = new SimpleObjectProperty<>();

    public RequestLabel() {
        super();

        init();
    }

    private void init() {
        getChildren().add(text);
        getChildren().add(hbox);

        hbox.getChildren().add(method);
        hbox.getChildren().add(resource);

        hbox.getStyleClass().clear();
        hbox.getStyleClass().add("httpio-controls-request-label__method-resource-wrapper");

        method.getStyleClass().clear();
        method.getStyleClass().add("httpio-controls-request-label__method");

        resource.getStyleClass().clear();
        resource.getStyleClass().add("httpio-controls-request-label__resource");

        request.addListener(new ChangeListener<Request>() {
            @Override
            public void changed(ObservableValue<? extends Request> observable, Request old, Request request) {
                loadRequest(old, request);
            }
        });
    }

    private void loadRequest(Request previous, Request request) {
        text.textProperty().bind(request.nameProperty());

        method.textProperty().bind(new StringBinding() {
            {
                super.bind(request.methodProperty());
            }

            @Override
            protected String computeValue() {
                Method method = request.getMethod();

                if (method == null) {
                    return null;
                }

                return method.getId().toString();
            }
        });

        resource.textProperty().bind(request.resourceProperty());
    }

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
