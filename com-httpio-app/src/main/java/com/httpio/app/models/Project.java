package com.httpio.app.models;

import com.httpio.app.modules.Item;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Project {
    private SimpleStringProperty id = new SimpleStringProperty();
    private SimpleStringProperty name = new SimpleStringProperty();
    private SimpleStringProperty description = new SimpleStringProperty();

    /**
     * Selected profile.
     */
    private ObjectProperty<Profile> profile = new SimpleObjectProperty<>();

    /**
     * Selected request.
     */
    private ObjectProperty<Request> request = new SimpleObjectProperty<>();

    private ListProperty<Profile> profiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ListProperty<Request> requests = new SimpleListProperty<>(FXCollections.observableArrayList());

    private boolean reloadRequestsTree = false;

    /**
     * Constructors
     */
    public Project() {
        this.id.setValue(UUID.randomUUID().toString());
    }

    public Project(String id) {
        this.id.setValue(id);
    }

    public Project(String id, String name, String description) {
        this.id.setValue(id);
        this.name.setValue(name);
        this.description.setValue(description);
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

    @SuppressWarnings("unused")
    public SimpleStringProperty idProperty() {
        return id;
    }

    /**
     * Name
     */
    public String getName() {
        return name.getValue();
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    /**
     * Description
     */
    public String getDescription() {
        return description.getValue();
    }

    public void setDescription(String description) {
        this.description.setValue(description);
    }

    @SuppressWarnings("unused")
    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    /**
     * Active profile
     */
    public void setProfile(Profile profile) {
        this.profile.set(profile);
    }

    public Profile getProfile() {
        return profile.get();
    }

    public ObjectProperty<Profile> profileProperty() {
        return profile;
    }

    /**
     * Requests
     */
    @SuppressWarnings("unused")
    public void setRequests(ObservableList<Request> requests) {
        for(Request request: requests) {
            request.setParent(null);
        }

        this.requests.setAll(requests);
    }

    public ObservableList<Request> getRequests() {
        return requests.get();
    }

    @SuppressWarnings("unused")
    public ListProperty<Request> requestsProperty() {
        return requests;
    }

    public void addRequest(Request request) {
        requests.add(request);

        request.setParent(null);
    }

    public void removeRequest(Request request) {
        requests.remove(request);

        request.setParent(null);

        if (this.request.getValue() == request) {
            this.request.setValue(this.requests.getValue().get(0));
        }
    }

    /**
     * Profiles
     */
    public ObservableList<Profile> getProfiles() {
        return profiles.get();
    }

    public void addProfile(Profile profile) {
        profiles.add(profile);
    }

    /**
     * Remove profile. If removed profile is active, then
     * active profile is set null.
     */
    public void removeProfile(Profile profile) {
        profiles.remove(profile);

        if (this.profile.getValue() == profile) {
            this.profile.setValue(null);
        }
    }

    public ListProperty<Profile> profilesProperty() {
        return profiles;
    }

    /**
     * Removed profile can be active. So, there is need to set new
     * active profile for project.
     */
    public void reloadProfileAfterRemove() {
        if (this.profile.getValue() == null) {
            this.profile.setValue(profiles.get(0));
        }
    }

    /**
     * Active request
     */
    public void setRequest(Request request) {
        this.request.set(request);
    }

    public Request getRequest() {
        return request.get();
    }

    public void reloadRequestAfterRemove() {
        if (this.request.getValue() == null) {
            this.request.setValue(requests.getValue().get(1));
        }
    }

    public ObjectProperty<Request> requestProperty() {
        return request;
    }

    public String getChecksum() throws NoSuchAlgorithmException {
        StringBuilder values = new StringBuilder(id.getValue() + name.getValue() + description.getValue());

        for(Profile profile: profiles) {
            values.append(profile.getId());
            values.append(profile.getName());
            values.append(profile.getBaseURL());
            values.append(profile.getDescription());

            for(Item variable: profile.getVariables()) {
                values.append(variable.getId());
                values.append(variable.getName());
                values.append(variable.getValue());
            }

            for(Item header: profile.getHeaders()) {
                values.append(header.getId());
                values.append(header.getName());
                values.append(header.getValue());
            }

            for(Item parameter: profile.getParameters()) {
                values.append(parameter.getId());
                values.append(parameter.getName());
                values.append(parameter.getValue());
            }
        }

        values.append(serializeRequests(requests));

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

    private String serializeRequests(ObservableList<Request> requests) {
        StringBuilder values = new StringBuilder();

        for(Request request: requests) {
            values.append(request.getId());
            values.append(request.getName());
            values.append(request.getUrl());
            values.append(request.getBody());
            values.append(request.isStandalone());

            if (request.getMethod() != null) {
                values.append(request.getMethod().getId());
            }

            if (request.getParent() != null) {
                values.append(request.getParent().getId());
            }

            for(Item header: request.getHeaders()) {
                values.append(header.getId());
                values.append(header.getName());
                values.append(header.getValue());
            }

            for(Item parameter: request.getParameters()) {
                values.append(parameter.getId());
                values.append(parameter.getName());
                values.append(parameter.getValue());
            }

            values.append(serializeRequests(request.getRequests()));
        }

        return values.toString();
    }

    @SuppressWarnings("unused")
    public void dump() {
        System.out.println("id " + id.getValue());
        System.out.println("name " + name.getValue());
        System.out.println("description " + description.getValue());

        System.out.println("Profiles");

        for(Profile profile: profiles) {
            System.out.print(" " + profile.getId());
            System.out.print(" - " + profile.getName());
            System.out.print(" - " + profile.getBaseURL());
            System.out.print(" - " + profile.getDescription());

            System.out.println();

            // Headers
            System.out.println("  Headers");
            for(Item item: profile.getHeaders()) {
                System.out.println("  " + item.getId() + " - " + item.getName() + " - " + item.getValue());
            }

            // Variables
            System.out.println("  Variables");
            for(Item item: profile.getVariables()) {
                System.out.println("  " + item.getId() + " - " + item.getName() + " - " + item.getValue());
            }

            // Parameter
            System.out.println("  Parameters");
            for(Item item: profile.getParameters()) {
                System.out.println("  " + item.getId() + " - " + item.getName() + " - " + item.getValue());
            }

            System.out.println();
        }

        // Requests
        System.out.println("Requests");

        dumpRequests(requests.getValue(), " ");
    }

    private void dumpRequests(ObservableList<Request> requests, String indent) {
        for(Request request: requests) {
            System.out.print(indent + request.getId());

            if (request.getMethod() != null) {
                System.out.print(" - " + request.getMethod().getId());
            } else {
                System.out.print(" - " + request.getMethod());
            }

            System.out.print(" - " + request.getName());
            System.out.print(" - " + request.getUrl());
            System.out.print(" - " + request.getBody());
            System.out.print(" - " + request.getInheritResource());
            System.out.println();

            // Headers
            System.out.println(indent + "Headers");
            for(Item item: request.getHeaders()) {
                System.out.println("  " + item.getId() + " - " + item.getName() + " - " + item.getValue());
            }

            // Parameter
            System.out.println(indent + "Parameters");

            for(Item item: request.getParameters()) {
                System.out.println("  " + item.getId() + " - " + item.getName() + " - " + item.getValue());
            }

            System.out.println();

            dumpRequests(request.getRequests(), indent + " ");
        }
    }

    public boolean isReloadRequestsTree() {
        return reloadRequestsTree;
    }

    public void setRequestsTreeStructure(Request.RequestsStaticNode root) {
        reloadRequestsTree = true;

        // Clean all requests.
        requests.clear();

        for(Request.RequestsStaticNode child: root.getChilds()) {
            Request request = child.getRequest();

            request.setParent(null);

            requests.add(request);

            // Parse childs requests
            setRequestsTreeStructureFor(child);
        }

        reloadRequestsTree = false;
    }

    private void setRequestsTreeStructureFor(Request.RequestsStaticNode node) {
        Request parent = node.getRequest();

        for(Request.RequestsStaticNode child: node.getChilds()) {
            Request request = child.getRequest();

            request.setParent(parent);

            parent.addRequest(request);

            // Parse childs requests
            setRequestsTreeStructureFor(child);
        }
    }
}
