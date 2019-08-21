package com.httpio.app.models;

import com.httpio.app.modules.Item;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@SuppressWarnings("unused")
public class Project {

    /**
     * Fields
     */
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

    /**
     * List of profiles.
     */
    private ListProperty<Profile> profiles = new SimpleListProperty<>(FXCollections.observableArrayList());

    /**
     * List of requests.
     */
    private ListProperty<Request> requests = new SimpleListProperty<>(FXCollections.observableArrayList());

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

    public String getId() {
        return id.getValue();
    }

    public void setId(String id) {
        this.id.setValue(id);
    }

    public SimpleStringProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.getValue();
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getDescription() {
        return description.getValue();
    }

    public void setDescription(String description) {
        this.description.setValue(description);
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    /**
     * Set active profile for profile.
     */
    public void setProfile(Profile profile) {
        this.profile.set(profile);
    }

    /**
     * Get active profile.
     */
    public Profile getProfile() {
        return profile.get();
    }

    /**
     * Get active profile property.
     */
    public ObjectProperty<Profile> profileProperty() {
        return profile;
    }

    public void setRequests(ObservableList<Request> requests) {
        this.requests.set(requests);
    }

    public ObservableList<Request> getRequests() {
        return requests.get();
    }

    public ListProperty<Request> requestsProperty() {
        return requests;
    }

    public void addRequest(Request request) {
        requests.add(request);
    }

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
    public void setProfileAfterRemove() {
        if (this.profile.getValue() == null) {
            this.profile.setValue(profiles.get(0));
        }
    }

    public void setRequest(Request request) {
        this.request.set(request);
    }

    public Request getRequest() {
        return request.get();
    }

    public void removeRequest(Request request) {
        requests.remove(request);

        if (this.request.getValue() == request) {
            this.request.setValue(this.requests.getValue().get(0));
        }
    }

    public void setRequestAfterRemove() {
        if (this.request.getValue() == null) {
            this.request.setValue(requests.getValue().get(1));
        }
    }

    public ObjectProperty<Request> requestProperty() {
        return request;
    }

    public String getChecksum() {
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
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        md.update(values.toString().getBytes());

        byte[] digest = md.digest();

        return DatatypeConverter.printHexBinary(digest).toUpperCase();
    }

    private String serializeRequests(ObservableList<Request> requests) {
        String values = "";

        for(Request request: requests) {
            values += request.getId();
            values += request.getName();
            values += request.getUrl();
            values += request.getBody();
            values += request.isStandalone();

            if (request.getMethod() != null) {
                values += request.getMethod().getId().toString();
            }

            if (request.getParent() != null) {
                values += request.getParent().getId();
            }

            for(Item header: request.getHeaders()) {
                values += header.getId();
                values += header.getName();
                values += header.getValue();
            }

            for(Item parameter: request.getParameters()) {
                values += parameter.getId();
                values += parameter.getName();
                values += parameter.getValue();
            }

            values += serializeRequests(request.getRequests());
        }

        return values;
    }

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
}
