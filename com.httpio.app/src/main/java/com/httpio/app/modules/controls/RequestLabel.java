package com.httpio.app.modules.controls;

import com.httpio.app.models.Request;
import com.httpio.app.modules.ListenersContainer;
import com.httpio.http.Method;
import com.httpio.http.Response;
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
    private Text methodField;

    @FXML
    private Text urlField;

    @FXML
    private Badge codeField;

    /**
     * Listener container.
     */
    private ListenersContainer listenersContainer = new ListenersContainer();

    /**
     * Present request.
     */
    private ObjectProperty<Request> request = new SimpleObjectProperty<>();

    /**
     * Last request before change.
     */
    private Request requestLast;

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

        } catch (IOException e) {
            e.printStackTrace();
        }

        init();
    }

    private void init() {
        request.addListener(new ChangeListener<Request>() {
            @Override
            public void changed(ObservableValue<? extends Request> observable, Request old, Request request) {

                reloadRequest();
            }
        });
    }

    private void reloadRequest() {
        listenersContainer.detachAll();

        if (this.request.getValue() == null) {
            reloadMethod();
            reloadCode();

            return;
        }

        Request request = this.request.getValue();

        nameField.textProperty().bind(request.nameProperty());

        listenersContainer.attach(request.methodProperty(), new ChangeListener<Method>() {
            @Override
            public void changed(ObservableValue<? extends Method> observable, Method old, Method method) {
                reloadMethod();
            }
        });

        listenersContainer.attach(request.lastResponseProperty(), new ChangeListener<Response>() {
            @Override
            public void changed(ObservableValue<? extends Response> observable, Response oldValue, Response newValue) {
                reloadCode();
            }
        });

        reloadMethod();
        reloadCode();

        urlField.textProperty().bind(request.urlProperty());
    }

    private void reloadMethod() {
        Request request = this.request.getValue();

        if (request == null) {
            methodField.setText(null);
        } else {
            methodField.setText(request.getMethod().getHTTPValue());
        }
    }

    private void reloadCode() {
        Request request = this.request.getValue();

        codeField.setText(null);
        codeField.setVisible(false);

        if (request == null) {
            return;
        }

        Response lastResponse = request.getLastResponse();

        if (lastResponse == null) {
            return;
        }

        codeField.setVisible(true);

        if (lastResponse.getCode() != null) {
            int code = lastResponse.getCode().getValueInt();

            codeField.setText(String.valueOf(code));

            if (code >= 200 && code < 300) {
                codeField.setColor(Color.LIGHTGREEN);
            } else {
                codeField.setColor(Color.CRIMSON);
            }
        } else {
            if (lastResponse.isException()) {
                codeField.setText("EX");
                codeField.setColor(Color.CRIMSON);
            }
        }
    }

    /**
     * StandardRequest
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
