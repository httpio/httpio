package com.httpio.app.util;

import com.httpio.app.models.Project;
import com.httpio.app.models.Request;

public class GenericWrapper {
    Project project;
    Request request;

    public GenericWrapper(Project project) {
        this.project = project;
    }

    public GenericWrapper(Request request) {
        this.request = request;
    }

    public Boolean isProject() {
        return project != null;
    }

    public Boolean isRequest() {
        return request != null;
    }

    public Project getProject() {
        return project;
    }

    public Request getRequest() {
        return request;
    }
}
