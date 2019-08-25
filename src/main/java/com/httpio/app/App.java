package com.httpio.app;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.httpio.app.modules.views.JsonTreeView;
import com.httpio.app.services.Logger;
import com.httpio.app.views.layout.LayoutView;
import com.httpio.app.views.profiles.ProfilesView;
import com.httpio.app.views.project.ProjectView;
import com.httpio.app.models.Project;
import com.httpio.app.services.ProjectSupervisor;
import com.httpio.app.modules.Components;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    private ProjectSupervisor projectSupervisor;
    private Stage stage;
    private Scene scene;
    private Components components;
    private Injector injector;
    private LayoutView layoutView;

    /**
     * Views
     */
    ProjectView projectView;
    ProfilesView profilesView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Save stage
        this.stage = stage;

        // setUserAgentStylesheet(STYLESHEET_CASPIAN);
        setUserAgentStylesheet(STYLESHEET_MODENA);

        // Init components
        components = new Components();

        components.setApp(this);
        components.setStage(stage);

        // Init Guice injector
        injector = Guice.createInjector(components);

        // Init project supervisor
        projectSupervisor = injector.getInstance(ProjectSupervisor.class);
        projectSupervisor.loadJsonHolderProject();

        projectSupervisor.projectProperty().addListener(new ChangeListener<Project>() {
            @Override
            public void changed(ObservableValue<? extends Project> observable, Project old, Project project) {
                loadProject(project);
            }
        });

        projectSupervisor.projectFilePathProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String old, String path) {
                reloadStageTitle();
            }
        });

        reloadStageTitle();

        // Init layout
        layoutView = new LayoutView(injector);
        layoutView.load();

        // Logger
        Logger logger = injector.getInstance(Logger.class);
        logger.setStatusBar(layoutView.getController().getStatusBar());
        logger.log("Starting Httpio.");

        scene = new Scene(layoutView.getView(), 1700, 900);
        scene.getStylesheets().add(App.class.getResource("/style.css").toExternalForm());

        // new JMetro(JMetro.Style.LIGHT).applyTheme(root);

        // Inject Scene to container
        components.setScene(scene);

        stage.setScene(this.scene);
        stage.show();

        // JsonTreeView jsonTreeView = new JsonTreeView();
        // scene.setRoot(jsonTreeView);

        initViews();
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        // Platform.exit();
        System.exit(0);
    }

    private void reloadStageTitle() {
        String path = projectSupervisor.getProjectFilePath();

        if (path != null) {
            stage.setTitle("Httpio [" + path + "]");
        } else {
            stage.setTitle("Httpio");
        }
    }

    private void loadProject(Project project) {
        projectView.getController().setProject(project);
        profilesView.getController().setProject(project);
    }

    private void initViews() {
        try {
            projectView = new ProjectView(injector, projectSupervisor.getProject());
            projectView.load();

            profilesView = new ProfilesView(injector, projectSupervisor.getProject());
            profilesView.load();

            layoutView.getController().setView(projectView);
            layoutView.getController().setView(profilesView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
