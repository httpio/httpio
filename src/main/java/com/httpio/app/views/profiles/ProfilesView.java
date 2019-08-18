package com.httpio.app.views.profiles;

import com.google.inject.Injector;
import com.httpio.app.models.Project;
import com.httpio.app.modules.views.View;

import java.io.IOException;

public class ProfilesView extends View<ProfilesController> {
    Project project;

    public ProfilesView(Injector injector, Project project) throws IOException {
        super(injector);

        this.project = project;
    }

    @Override
    protected void initialize(ProfilesController controller) {
        super.initialize(controller);

        controller.setProject(project);
    }
}
