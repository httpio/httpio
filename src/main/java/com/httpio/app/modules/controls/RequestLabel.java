package com.httpio.app.modules.controls;

import com.httpio.app.models.Request;
import com.httpio.app.modules.ListenersContainer;
import com.httpio.app.services.HTTPSender;
import com.httpio.app.services.Http.Method;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;

public class RequestLabel extends HBox {
    @FXML
    private Text nameField;

    @FXML
    private Badge methodField;

    @FXML
    private Text urlField;

    @FXML
    private Badge codeField;

    private ListenersContainer listenersContainer = new ListenersContainer();

    private ObjectProperty<Request> request = new SimpleObjectProperty<>();

    /**
     * Constructors
     */
    public RequestLabel() {
        super();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RequestLabel.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();

        } catch (IOException exc) {
            exc.printStackTrace();
        }

        init();
    }

    private void init() {
        request.addListener((observable, old, request) -> {
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

        nameField.textProperty().bind(request.nameProperty());

        listenersContainer.attach(request.methodProperty(), new ChangeListener<Method>() {
            @Override
            public void changed(ObservableValue<? extends Method> observable, Method old, Method method) {
                reloadMethod();
            }
        });

        listenersContainer.attach(request.lastResponseProperty(), new ChangeListener<HTTPSender.Response>() {
            @Override
            public void changed(ObservableValue<? extends HTTPSender.Response> observable, HTTPSender.Response oldValue, HTTPSender.Response newValue) {
                reloadCode();
            }
        });

        reloadMethod();
        reloadCode();

        // method.textProperty().bind(new StringBinding() {
        //     {
        //         super.bind(request.methodProperty());
        //     }

        //     @Override
        //     protected Functions computeValue() {
        //         Method method = request.getMethod();

        //         if (method == null) {
        //             return null;
        //         }

        //         return method.getId().toString();
        //     }
        // });

        urlField.textProperty().bind(request.urlProperty());
    }

    private void reloadMethod() {
        if (request == null) {
            return;
        }

        methodField.setText(request.getValue().getMethod().getId().toString());
        methodField.setColor(request.getValue().getMethod().getColor());
    }

    private void reloadCode() {
        codeField.setText(null);
        codeField.setVisible(false);

        Request request = this.request.getValue();

        if (request == null) {
            return;
        }

        if (request.getLastResponse() == null) {
            return;
        }

        int code = request.getLastResponse().getCode();

        codeField.setVisible(true);
        codeField.setText(String.valueOf(code));

        if (code >= 200 && code < 300) {
            codeField.setColor(Color.LIGHTGREEN);
        } else {
            codeField.setColor(Color.CRIMSON);
        }
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
