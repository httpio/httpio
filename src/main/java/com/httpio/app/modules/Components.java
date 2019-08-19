package com.httpio.app.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.httpio.app.App;
import com.httpio.app.services.Http;
import com.httpio.app.services.Icons;
import com.httpio.app.services.Logger;
import com.httpio.app.services.ProjectSupervisor;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Components extends AbstractModule {
    Stage stage;
    Scene scene;
    App app;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void setApp(App app) {
        this.app = app;
    }

    @Override
    protected void configure() {
        bind(ProjectSupervisor.class).in(Singleton.class);
        bind(Logger.class).in(Singleton.class);
        bind(Http.class).in(Singleton.class);
        bind(Icons.class).in(Singleton.class);
    }

    @Provides
    Stage provideStage() {
        return stage;
    }

    @Provides
    Scene provideScene() {
        return scene;
    }

    @Provides
    App provideApp() {
        return app;
    }
}
