package com.httpio.app.routes;

import com.httpio.app.models.Project;
import com.httpio.app.modules.router.RouteInterface;

public class RouteProfiles implements RouteInterface {
    Project project;

    public RouteProfiles(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }
}
