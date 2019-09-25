package com.httpio.app.views.layout;

import com.google.inject.Inject;
import com.google.inject.spi.RequireExactBindingAnnotationsOption;
import com.httpio.app.App;
import com.httpio.app.models.Project;
import com.httpio.app.models.Request;
import com.httpio.app.modules.ListenersContainer;
import com.httpio.app.services.Icons;
import com.httpio.app.services.ProjectSupervisor;
import com.httpio.app.modules.ControllerInterface;
import com.httpio.app.util.GenericWrapper;
import com.httpio.app.util.NodeHelper;
import com.httpio.app.views.profiles.ProfilesView;
import com.httpio.app.views.project.ProjectView;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.control.StatusBar;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class LayoutController implements ControllerInterface {
    private final String SCOPE_ACTIVE_PROJECT = "ActiveProject";

    /**
     * UI
     */
    @FXML
    private MenuBar menuBar;

    @FXML
    private VBox contentContainer;

    @FXML
    private StatusBar statusBar;

    /**
     * Menu items
     */
    MenuItem menuItemFileSave;

    /**
     * Views
     */
    ProjectView projectView;

    /**
     * Fields
     */
    private Stage stage;
    private App app;
    private ProjectSupervisor projectSupervisor;
    private Icons icons;
    private ListenersContainer listenersContainer = new ListenersContainer();

    private Project project;
    private Request request;

    @Inject
    public void setProjectSupervisor(ProjectSupervisor projectSupervisor) {
        this.projectSupervisor = projectSupervisor;
    }

    @Inject
    public void setIcons(Icons icons) {
        this.icons = icons;
    }

    @Inject
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Inject
    public void setApp(App app) {
        this.app = app;
    }

    public void setView(ProjectView projectView) {
        this.projectView = projectView;

        contentContainer.getChildren().setAll(projectView.getView());
    }

    // public Text getStatusBar() {
    //     return statusBar;
    // }

    @Override
    public void prepare() {
        prepareMenuBar();

        listenersContainer.attach(projectSupervisor.projectProperty(), new ChangeListener<Project>() {
            @Override
            public void changed(ObservableValue<? extends Project> observableValue, Project previous, Project project) {
                setProject(project);
            }
        }, SCOPE_ACTIVE_PROJECT);

        if (projectSupervisor.getProject() != null) {
            setProject(projectSupervisor.getProject());
        }
    }

    private void setProject(Project project) {
        listenersContainer.detach(SCOPE_ACTIVE_PROJECT);

        if (project == null) {
            return;
        }

        this.project = project;
    }

    private void prepareMenuBar() {
        menuBar.getMenus().clear();

        // Menu file
        Menu menu = new Menu("File");

        //File new
        MenuItem menuItemFileNew = new MenuItem("New");
        icons.attachIcon(menuItemFileNew, Icons.ICON_NEW_FILE);

        menuItemFileNew.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));

        // File open
        MenuItem menuItemFileOpen = new MenuItem("Open");
        icons.attachIcon(menuItemFileOpen, Icons.ICON_OPEN);

        menuItemFileOpen.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));

        // File save
        menuItemFileSave = new MenuItem("Save");
        icons.attachIcon(menuItemFileSave, Icons.ICON_SAVE);

        menuItemFileSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));

        // File save as
        MenuItem menuItemFileSaveAs = new MenuItem("Save as");
        icons.attachIcon(menuItemFileSaveAs, Icons.ICON_SAVE_AS);

        // File close
        MenuItem menuItemFileClose = new MenuItem("Close");
        icons.attachIcon(menuItemFileClose, Icons.ICON_CLOSE);

        // Events
        menuItemFileNew.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                projectSupervisor.loadNewProject();
            }
        });

        menuItemFileClose.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    app.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        menuItemFileOpen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();

                fileChooser.setTitle("Open project");
                fileChooser.getExtensionFilters().addAll(
                    // new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                    // new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                    new FileChooser.ExtensionFilter("Project file (*.xml)", "*.xml")
                    // new FileChooser.ExtensionFilter("All Files", "*.*")
                );

                File selectedFile = fileChooser.showOpenDialog(stage);

                if (selectedFile != null) {
                    try {
                        projectSupervisor.load(selectedFile.getPath());
                    } catch (Exception e) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);

                        alert.setTitle("Open project error");
                        // alert.setHeaderText(" error");
                        alert.setContentText(e.getMessage());

                        alert.showAndWait();

                        e.printStackTrace();
                    }
                }
            }
        });

        menuItemFileSave.setOnAction(event -> {
            try {
                projectSupervisor.save();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);

                alert.setTitle("Save project error");
                alert.setContentText(e.getMessage());

                alert.showAndWait();

                e.printStackTrace();
            }
        });

        projectSupervisor.projectFilePathProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String old, String path) {
                reloadMenuSave();
            }
        });

        reloadMenuSave();

        // Save as
        menuItemFileSaveAs.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();

            fileChooser.setTitle("Save project as ...");

            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML (*.xml)", "*.xml"));
            fileChooser.setInitialFileName("*.xml");

            File selectedFile = fileChooser.showSaveDialog(stage);

            if (selectedFile != null) {
                try {
                    projectSupervisor.save(selectedFile.getPath());
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);

                    alert.setTitle("Save project error");
                    alert.setContentText(e.getMessage());

                    alert.showAndWait();

                    e.printStackTrace();
                }
            }
        });

        menu.getItems().add(menuItemFileNew);
        menu.getItems().add(menuItemFileOpen);
        menu.getItems().add(menuItemFileSave);
        menu.getItems().add(menuItemFileSaveAs);
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(menuItemFileClose);

        // Attach events.
        menuBar.getMenus().add(menu);
    }

    private void reloadMenuSave() {
        if (projectSupervisor.getProjectFilePath() == null) {
            menuItemFileSave.setDisable(true);
        } else {
            menuItemFileSave.setDisable(false);
        }
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }
}
