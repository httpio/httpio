package com.httpio.app.models;

import com.httpio.app.services.Http.Method;
import com.httpio.app.modules.Item;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.UUID;

public class Request {
    private SimpleStringProperty id = new SimpleStringProperty();
    private SimpleObjectProperty<Method> method = new SimpleObjectProperty<>();
    private SimpleStringProperty resource = new SimpleStringProperty();
    private SimpleStringProperty url = new SimpleStringProperty();

    private SimpleStringProperty name = new SimpleStringProperty();
    private SimpleStringProperty body = new SimpleStringProperty();
    private SimpleBooleanProperty inheritResource = new SimpleBooleanProperty(true);
    private SimpleBooleanProperty standalone = new SimpleBooleanProperty(false);

    private Request parent;

    private ListProperty<Item> headers = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ListProperty<Item> parameters = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ListProperty<Request> requests = new SimpleListProperty<>(FXCollections.observableArrayList());

    /**
     * Constructor.
     */
    public Request() {
        this.id.setValue(UUID.randomUUID().toString());
    }

    public Request(String id) {
        this.id.setValue(id);
    }

    public Request(String id, String name) {
        this.id.setValue(id);
        this.name.setValue(name);
    }

    /**
     * Standalone
     */
    public boolean isStandalone() {
        return standalone.get();
    }

    public SimpleBooleanProperty standaloneProperty() {
        return standalone;
    }

    public void setStandalone(boolean standalone) {
        this.standalone.set(standalone);
    }

    /**
     * URL
     */
    public String getUrl() {
        return url.get();
    }

    public String getURLFull() {
        String t = url.getValue() == null ? "" : url.getValue();
        String p = parent == null ? "" : parent.getURLFull();

        if (!standalone.getValue()) {
            return p + t;
        } else {
            return t;
        }
    }

    public SimpleStringProperty urlProperty() {
        return url;
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    /**
     * Init request with ID, name and parent.
     *
     * @param id
     * @param name
     * @param parent
     */
    public Request(String id, String name, Request parent) {
        this.id.setValue(id);
        this.name.setValue(name);

        this.parent = parent;
    }

    public Request(String id, Method method, String resource, String name) {
        this.id.setValue(id);
        this.name.setValue(name);

        this.resource.setValue(resource);
        this.method.setValue(method);
    }

    public Request(String id, Method method, String resource, String name, Request parent) {
        this.id.setValue(id);
        this.resource.setValue(resource);
        this.name.setValue(name);
        this.parent = parent;
        this.method.setValue(method);
    }

    /**
     * Returns ID of request.
     *
     * @return
     */
    public String getId() {
        return id.getValue();
    }

    /**
     * Set ID.
     *
     * @param id
     */
    public void setId(String id) {
        this.id.setValue(id);
    }

    public SimpleStringProperty idProperty() {
        return id;
    }

    /**
     * Set if request inherits resouce path from parent.
     *
     * @param inheritResource
     */
    public void setInheritResource(Boolean inheritResource) {
        this.inheritResource.setValue(inheritResource);
    }

    public Boolean getInheritResource() {
        return inheritResource.getValue();
    }

    public SimpleBooleanProperty inheritResourceProperty() {
        return inheritResource;
    }

    /**
     * Set name.
     *
     * @param name
     */
    public void setName(String name) {
        this.name.setValue(name);
    }

    /**
     * Returns name of request.
     * @return
     */
    public String getName() {
        return name.getValue();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    /**
     * Returns method.
     *
     * @return
     */
    public Method getMethod() {
        return method.get();
    }

    /**
     * Set method.
     *
     * @param method
     */
    public void setMethod(Method method) {
        this.method.set(method);
    }

    /**
     * Returns method property.
     *
     * @return
     */
    public SimpleObjectProperty<Method> methodProperty() {
        return method;
    }

    /**
     * Return resource of request. If request is child, then resource will contain part of parent resource.
     *
     * @return
     */
    public String getResource() {
        return resource.getValue();
    }

    public void setResource(String resource) {
        this.resource.setValue(resource);
    }

    public SimpleStringProperty resourceProperty() {
        return resource;
    }

    public void setHeaders(ObservableList<Item> headers) {
        this.headers.setAll(headers);
    }

    public ObservableList<Item> getHeaders() {
        return headers.get();
    }

    public ListProperty<Item> headersProperty() {
        return headers;
    }

    /**
     * Add header.
     *
     * @param header
     */
    public void addHeader(Item header) {
        headers.add(header);
    }

    public void addHeader(String name, String value) {
        Item item = new Item();
        item.setName(name);
        item.setValue(value);

        headers.add(item);
    }

    public void addHeader(String name) {
        Item item = new Item();
        item.setName(name);

        headers.add(item);
    }

    /**
     * Remove header.
     *
     * @param header
     */
    public void removeHeader(Item header) {
        headers.remove(header);
    }

    /**
     * Set request.
     *
     * @param requests
     */
    public void setRequests(ObservableList<Request> requests) {
        this.requests.setAll(requests);
    }

    /**
     * Returns requests.
     *
     * @return
     */
    public ObservableList<Request> getRequests() {
        return requests.get();
    }

    /**
     * Returns requests property.
     *
     * @return
     */
    public ListProperty<Request> requestsProperty() {
        return requests;
    }

    /**
     * Add request.
     *
     * @param request
     */
    public void addRequest(Request request) {
        // Set parent
        request.setParent(this);

        // Add request to list
        requests.add(request);
    }

    /**
     * Remove request.
     *
     * @param request
     */
    public void removeRequest(Request request) {
        requests.remove(request);

        request.setParent(null);
    }

    /**
     * Set parent.
     *
     * @param parent
     */
    public void setParent(Request parent) {
        this.parent = parent;

        // // Set id of parent to for export.
        // if (parent == null) {
        //     parentId = null;
        // } else {
        //     parentId = parent.getId();
        // }
    }

    /**
     * Returns reference to parent request.
     *
     * @return
     */
    public Request getParent() {
        return parent;
    }

    /**
     * Return parameters.
     *
     * @return
     */
    public ObservableList<Item> getParameters() {
        return parameters.get();
    }

    /**
     * Set parameters.
     *
     * @param parameters
     */
    public void setParameters(ObservableList<Item> parameters) {
        this.parameters.setAll(parameters);
    }

    /**
     * Returns parameters property.
     *
     * @return
     */
    public ListProperty<Item> parametersProperty() {
        return parameters;
    }

    /**
     * Add parameters to list.
     *
     * @param parameter
     */
    public void addParameter(Item parameter) {
        this.parameters.add(parameter);
    }

    public void addParameter(String name, String value) {
        Item item = new Item();

        item.setName(name);
        item.setValue(value);

        this.parameters.add(item);
    }

    public void addParameter(String name) {
        Item item = new Item();

        item.setName(name);

        this.parameters.add(item);
    }

    /**
     * Add parameters to list.
     *
     * @param parameter
     */
    public void removeParameter(Item parameter) {
        this.parameters.remove(parameter);
    }

    /**
     * Set body of request.
     *
     * @param body
     */
    public void setBody(String body) {
        this.body.setValue(body);
    }

    /**
     * Return content of body.
     *
     * @return
     */
    public String getBody() {
        return body.getValue();
    }

    public SimpleStringProperty bodyProperty() {
        return body;
    }
}