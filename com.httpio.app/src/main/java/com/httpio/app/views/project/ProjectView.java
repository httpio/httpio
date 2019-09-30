package com.httpio.app.views.project;

import com.google.inject.Injector;
import com.httpio.app.models.Project;
import com.httpio.app.modules.views.View;

import java.io.IOException;

public class ProjectView extends View<ProjectController> {
    /**
     * Project to display
     */
    private Project project;

    public ProjectView(Injector injector, Project project) {
        super(injector);

        this.project = project;
    }

    @Override
    protected void initialize(ProjectController controller) {
        super.initialize(controller);

        controller.setProject(project);
    }
}
