package com.httpio.app.models;

import com.httpio.app.modules.Item;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.UUID;

public class Profile {
    private StringProperty id = new SimpleStringProperty();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();
    private StringProperty baseURL = new SimpleStringProperty();

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
     * ID
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
     * Base URL
     */
    public String getBaseURL() {
        return baseURL.get();
    }

    public StringProperty baseURLProperty() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL.set(baseURL);
    }

    /**
     * Name.
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
     * Description.
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
     * Variables
     */
    public ObservableList<Item> getVariables() {
        return variables.get();
    }

    @SuppressWarnings("unused")
    public void setVariables(ObservableList<Item> variables) {
        this.variables.set(variables);
    }

    public ListProperty<Item> variablesProperty() {
        return variables;
    }

    public void addVariable(Item variable) {
        variables.add(variable);
    }

    public void addVariable(String name, String value) {
        Item item = new Item();

        item.setName(name);
        item.setValue(value);

        variables.add(item);
    }

    @SuppressWarnings("unused")
    public void removeVariable(Item variable) {
        variables.remove(variable);
    }

    /**
     * Headers
     */
    public ObservableList<Item> getHeaders() {
        return headers.get();
    }

    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
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
