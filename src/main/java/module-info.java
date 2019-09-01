module httpio {
    opens com.httpio.app to javafx.graphics, com.google.guice, javafx.fxml;
    opens com.httpio.app.views.layout to com.google.guice, javafx.fxml;
    opens com.httpio.app.views.project to javafx.fxml;
    opens com.httpio.app.modules.views to javafx.fxml;
    opens com.httpio.app.views.profiles to javafx.fxml;
    opens com.httpio.app.modules.controls to javafx.fxml;
    opens com.httpio.app.modules to javafx.base;

    exports com.httpio.app.services to com.google.guice;
    exports com.httpio.app.modules to com.google.guice;
    exports com.httpio.app.views.layout to com.google.guice;
    exports com.httpio.app.views.project to com.google.guice;
    exports com.httpio.app.modules.views to javafx.fxml;
    exports com.httpio.app.views.profiles;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;

    requires gson;
    requires java.xml;
    requires com.google.guice;
    requires java.base;

    // requires guice;
    requires org.jetbrains.annotations;

    requires org.apache.commons.text;

    // exports com.httpio.app;
}

