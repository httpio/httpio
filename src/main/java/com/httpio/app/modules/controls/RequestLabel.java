package com.httpio.app.modules.controls;

import com.httpio.app.models.Request;
import com.httpio.app.services.Http.Method;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class RequestLabel extends VBox {
    private HBox hbox = new HBox();

    private Text text = new Text();
    private Text method = new Text();
    private Text resource = new Text();

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
        hbox.getChildren().add(resource);

        hbox.getStyleClass().clear();
        hbox.getStyleClass().add("httpio-controls-request-label__method-resource-wrapper");

        method.getStyleClass().clear();
        method.getStyleClass().add("httpio-controls-request-label__method");

        resource.getStyleClass().clear();
        resource.getStyleClass().add("httpio-controls-request-label__resource");

        request.addListener((observable, old, request) -> {
            // loadRequest(old, request);
            loadRequest(request);
        });
    }

    private void loadRequest(Request request) {
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

        resource.textProperty().bind(request.urlProperty());
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
