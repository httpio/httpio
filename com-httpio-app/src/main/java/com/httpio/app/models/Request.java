package com.httpio.app.models;

import com.httpio.app.modules.Item;
import com.httpio.http.Method;
import com.httpio.http.Response;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import static com.httpio.app.util.Functions.ifnull;

public class Request {
    private SimpleStringProperty id = new SimpleStringProperty();
    private SimpleObjectProperty<Method> method = new SimpleObjectProperty<>();
    private SimpleStringProperty resource = new SimpleStringProperty();
    private SimpleStringProperty url = new SimpleStringProperty();

    private SimpleStringProperty name = new SimpleStringProperty();
    private SimpleStringProperty body = new SimpleStringProperty();
    private SimpleBooleanProperty inheritResource = new SimpleBooleanProperty(true);
    private SimpleBooleanProperty standalone = new SimpleBooleanProperty(false);

    private SimpleObjectProperty<Response> lastResponse = new SimpleObjectProperty<>();

    // private StandardRequest parent;
    private SimpleObjectProperty<Request> parent = new SimpleObjectProperty<>();

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
     * Init request with ID, name and parent.
     *
     * @param id
     * @param name
     * @param parent
     */
    public Request(String id, String name, Request parent) {
        this.id.setValue(id);
        this.name.setValue(name);

        this.parent.setValue(parent);
    }

    public Request duplicate() {
        Request duplicated = new Request();

        duplicated.setMethod(method.getValue());
        duplicated.setUrl(url.getValue());
        duplicated.setName(name.getValue());

        duplicated.setBody(body.getValue());
        duplicated.setStandalone(standalone.getValue());

        for(Item item: headers.getValue()) {
            duplicated.addHeader(item.getName(), item.getValue());
        }

        for(Item item: parameters.getValue()) {
            duplicated.addParameter(item.getName(), item.getValue());
        }

        // Copy requests
        for(Request request: requests.getValue()) {
            duplicated.addRequest(request.duplicate());
        }

        return duplicated;
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
        Request parent = this.parent.get();

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
        this.parent.setValue(parent);
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
        for(Request request: requests) {
            request.setParent(this);
        }

        this.requests.setAll(requests);
    }

    // public void setRequests(List<RequestsStaticNode> requests) {
    //     this.requests.clear();

    //     for(RequestsStaticNode requestsStaticNode: requests) {
    //         StandardRequest request = requestsStaticNode.getRequest();

    //         request.setParent(this);

    //         this.requests.add(request);

    //         request.setRequests(requestsStaticNode.getChilds());
    //     }
    // }

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
        this.parent.setValue(parent);
    }

    public void removeRequests() {
        requests.clear();

        for(Request request: requests) {
            request.setParent(null);
        }
    }

    public SimpleObjectProperty<Request> parentProperty() {
        return parent;
    }

    /**
     * Returns reference to parent request.
     *
     * @return
     */
    public Request getParent() {
        return parent.getValue();
    }

    public List<Request> getRequestsFlatPathToRoot() {
        ArrayList<Request> path = new ArrayList<>();

        path.add(this);

        Request pointer = getParent();

        while(pointer != null) {
            path.add(pointer);

            pointer = pointer.getParent();
        }

        return path;
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

    /**
     * Last response
     */
    public Response getLastResponse() {
        return lastResponse.get();
    }

    public SimpleObjectProperty<Response> lastResponseProperty() {
        return lastResponse;
    }

    public void setLastResponse(Response lastResponse) {
        this.lastResponse.set(lastResponse);
    }

    public String getChecksum() throws NoSuchAlgorithmException {
        return getChecksum(new Properties());
    }

    public String getChecksum(Properties params) throws NoSuchAlgorithmException {
        StringBuilder values = new StringBuilder();

        // Optional.ofNullable(gearBox).orElse("")
        if (!params.containsKey("excludeId")) {
            values.append(ifnull(id.getValue(), ""));
        }

        if (method.getValue() != null) {
            // values.append(method.getValue().getId().toString());
            values.append(method.getValue().getHTTPValue());
        }

        values.append(ifnull(url.getValue(), ""));
        values.append(ifnull(name.getValue(), ""));
        values.append(ifnull(body.getValue(), ""));

        if (standalone.getValue()) {
            values.append("1");
        } else {
            values.append("0");
        }

        if (parent != null) {
            if (!params.containsKey("excludeId")) {
                values.append(parent.getValue().getId());
            }

            // values.append(parent.getChecksum(params));
        }

        for(Item item: headers.getValue()) {
            if (!params.containsKey("excludeId")) {
                values.append(ifnull(item.getId(), ""));
            }

            values.append(ifnull(item.getName(), ""));
            values.append(ifnull(item.getValue(), ""));
        }

        for(Item item: parameters.getValue()) {
            if (!params.containsKey("excludeId")) {
                values.append(ifnull(item.getId(), ""));
            }

            values.append(ifnull(item.getName(), ""));
            values.append(ifnull(item.getValue(), ""));
        }

        for(Request request: requests.getValue()) {
            values.append(request.getChecksum(params));
        }

        // Create checksum
        MessageDigest md = MessageDigest.getInstance("MD5");

        md.update(values.toString().getBytes());

        byte[] digest = md.digest();

        StringBuilder sb = new StringBuilder();

        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString().toUpperCase();
    }

    public static class RequestsStaticNode {
        private Request request;
        private List<RequestsStaticNode> childs = new ArrayList<>();

        public RequestsStaticNode() {}

        public RequestsStaticNode(Request request) {
            this.request = request;
        }

        public List<RequestsStaticNode> getChilds() {
            return childs;
        }

        public Request getRequest() {
            return request;
        }

        public void add(RequestsStaticNode node) {
            childs.add(node);
        }
    }
}
