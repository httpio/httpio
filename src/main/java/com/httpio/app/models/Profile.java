package com.httpio.app.models;

import com.httpio.app.modules.Item;
import com.httpio.app.services.Http.Protocol;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.UUID;

public class Profile {
    private StringProperty id = new SimpleStringProperty();
    private StringProperty name = new SimpleStringProperty();
    private ObjectProperty<Protocol> protocol = new SimpleObjectProperty<>();
    private StringProperty username = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private StringProperty host = new SimpleStringProperty();
    private StringProperty port = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();

    private ListProperty<Item> variables = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ListProperty<Item> headers = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ListProperty<Item> parameters = new SimpleListProperty<>(FXCollections.observableArrayList());

    /**
     * Constructors
     */
    public Profile() {
        id.setValue(UUID.randomUUID().toString());
    }

    public Profile(String id) {
        this.id.setValue(id);
    }

    public Profile(String id, String name) {
        this.id.setValue(id);
        this.name.setValue(name);
    }

    @Override
    public String toString() {
        return this.getName();
    }

    /**
     * Returns ID.
     *
     * @return
     */
    public String getId() {
        return id.getValue();
    }

    public void setId(String id) {
        this.id.setValue(id);
    }

    public StringProperty idProperty() {
        return id;
    }

    /**
     * Returns name.
     *
     * @return
     */
    public String getName() {
        return name.getValue();
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    /**
     * Returns description.
     *
     * @return
     */
    public String getDescription() {
        return description.getValue();
    }

    public void setDescription(String description) {
        this.description.setValue(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    /**
     * Returns protocol.
     *
     * @return
     */
    public Protocol getProtocol() {
        return protocol.getValue();
    }

    public void setProtocol(Protocol protocol) {
        this.protocol.setValue(protocol);
    }

    public ObjectProperty<Protocol> protocolProperty() {
        return protocol;
    }

    /**
     * Returns username.
     *
     * @return
     */
    public String getUsername() {
        return username.getValue();
    }

    public void setUsername(String username) {
        this.username.setValue(username);
    }

    public StringProperty usernameProperty() {
        return username;
    }

    /**
     * Returns password.
     *
     * @return
     */
    public String getPassword() {
        return password.getValue();
    }

    public void setPassword(String password) {
        this.password.setValue(password);
    }

    public StringProperty passwordProperty() {
        return password;
    }

    /**
     * Returns host.
     *
     * @return
     */
    public String getHost() {
        return host.getValue();
    }

    public void setHost(String host) {
        this.host.setValue(host);
    }

    public StringProperty hostProperty() {
        return host;
    }

    /**
     * Returns port
     *
     * @return
     */
    public String getPort() {
        return port.getValue();
    }

    public void setPort(String port) {
        this.port.setValue(port);
    }

    public StringProperty portProperty() {
        return port;
    }

    /**
     * Variables
     */
    public ObservableList<Item> getVariables() {
        return variables.get();
    }

    public void setVariables(ObservableList<Item> variables) {
        this.variables.set(variables);
    }

    public ListProperty<Item> variablesProperty() {
        return variables;
    }

    public void addVariable(Item variable) {
        variables.add(variable);
    }

    public void removeVariable(Item variable) {
        variables.remove(variable);
    }

    /**
     * Headers
     */
    public ObservableList<Item> getHeaders() {
        return headers.get();
    }

    public void setHeaders(ObservableList<Item> headers) {
        this.headers.set(headers);
    }

    public ListProperty<Item> headersProperty() {
        return headers;
    }

    public void addHeader(Item header) {
        headers.add(header);
    }

    public void removeHeader(Item header) {
        headers.remove(header);
    }

    /**
     * Parameters
     */
    public ObservableList<Item> getParameters() {
        return parameters.get();
    }

    public void setParameters(ObservableList<Item> parameters) {
        this.parameters.set(parameters);
    }

    public ListProperty<Item> parametersProperty() {
        return parameters;
    }

    public void addParameter(Item queryParameter) {
        parameters.add(queryParameter);
    }

    public void removeParameter(Item queryParameter) {
        parameters.remove(queryParameter);
    }
}
