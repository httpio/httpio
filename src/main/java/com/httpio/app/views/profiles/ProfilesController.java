package com.httpio.app.views.profiles;

import com.google.inject.Inject;
import com.httpio.app.models.Profile;
import com.httpio.app.models.Project;
import com.httpio.app.modules.ControllerInterface;
import com.httpio.app.modules.Item;
import com.httpio.app.modules.ListenersContainer;
import com.httpio.app.modules.views.TableViewNameValue;
import com.httpio.app.services.Http;
import com.httpio.app.services.Icons;
import com.httpio.app.services.ProjectSupervisor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.util.Optional;

public class ProfilesController implements ControllerInterface {
    private Project project;
    private Profile profile;
    private Http http;
    private Icons icons;
    private ListenersContainer listenersContainer = new ListenersContainer();
    private ProjectSupervisor projectSupervisor;

    /**
     * FXMl
     */
    @FXML
    private ListView<Profile> profilesListView;

    @FXML
    private Button newProfileButton;

    @FXML
    private TextField nameField;

    @FXML
    private TextField baseURLField;

    @FXML
    private TextArea descriptionTextField;

    @FXML
    private TableViewNameValue<Item> variablesTableView;

    @FXML
    private TableViewNameValue<Item> headersTableView;

    @FXML
    private TableViewNameValue<Item> parametersTableView;

    @FXML
    private SplitPane splitPane;

    @Inject
    public void setHttp(Http http) {
        this.http = http;
    }

    @Inject
    public void setIcons(Icons icons) {
        this.icons = icons;
    }

    public SplitPane getSplitPane() {
        return splitPane;
    }

    @Override
    public void prepare() {
        newProfileButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Profile profile = projectSupervisor.createNewProfile();

                project.profilesProperty().add(profile);

                loadProfile(profile);
            }
        });

        prepareProfilesListView();

        prepareVariablesTable();
        prepareHeadersTableView();
        prepareParametersTableView();
    }

    private void prepareProfilesListView() {

        profilesListView.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                Profile selected = profilesListView.getSelectionModel().getSelectedItem();

                if (selected != null) {
                    if (event.getCode() == KeyCode.F2) {
                        handleRename(selected);
                    } else if (event.getCode() == KeyCode.DELETE) {
                        handleDelete(selected);
                    }
                }
            }
        });

        profilesListView.setCellFactory(new Callback<ListView<Profile>, ListCell<Profile>>() {
            @Override
            public ListCell<Profile> call(ListView<Profile> profileListView) {
                return new ListCell<Profile>() {
                    Text text = new Text();

                    @Override
                    protected void updateItem(Profile profile, boolean empty) {
                        super.updateItem(profile, empty);

                        text.textProperty().unbind();

                        if (empty) {
                            setGraphic(null);
                        } else {
                            text.textProperty().bind(profile.nameProperty());

                            attachContextMenuToProfileListView(this, profile);

                            setGraphic(text);
                        }
                    }
                };
            }
        });
    }

    private void attachContextMenuToProfileListView(ListCell<Profile> cell, Profile profile) {
        // Rename
        MenuItem itemRename = new MenuItem("Rename");
        icons.attachIcon(itemRename, Icons.ICON_RENAME);

        itemRename.setAccelerator(new KeyCodeCombination(KeyCode.F2));

        // Delete
        MenuItem itemDelete = new MenuItem("Delete");
        icons.attachIcon(itemDelete, Icons.ICON_REMOVE);

        // itemDelete.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));

        // Events
        itemRename.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleRename(profile);
            }
        });

        itemDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleDelete(profile);
            }
        });

        ContextMenu contextMenu = new ContextMenu();

        contextMenu.getItems().add(itemRename);

        if (project.getProfiles().size() > 1) {
            contextMenu.getItems().add(itemDelete);
        }

        cell.setContextMenu(contextMenu);
    }

    private void handleRename(Profile profile) {
        TextInputDialog dialog = new TextInputDialog(profile.getName());

        dialog.setTitle("Change name of profile");
        dialog.setHeaderText("Please put new name of profile.");

        Optional<String> result = dialog.showAndWait();

        profile.setName(result.get());

        profilesListView.refresh();
    }

    private void handleDelete(Profile profile) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + profile.getName() + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            project.removeProfile(profile);
            project.reloadProfileAfterRemove();
        }
    }

    private void prepareVariablesTable() {
        variablesTableView.setIcons(icons);

        variablesTableView.setItemFactory(new Callback<String, Item>() {
            @Override
            public Item call(String s) {
                Item item = new Item();

                item.setName(s);

                return item;
            }
        });

        variablesTableView.setItemRemover(new Callback<Item, Object>() {
            @Override
            public Object call(Item item) {
                profile.removeVariable(item);

                return null;
            }
        });
    }

    private void prepareHeadersTableView() {
        headersTableView.setIcons(icons);

        headersTableView.setItemFactory(new Callback<String, Item>() {
            @Override
            public Item call(String s) {
                Item item = new Item();

                item.setName(s);

                return item;
            }
        });

        headersTableView.setItemRemover(new Callback<Item, Object>() {
            @Override
            public Object call(Item item) {
                profile.removeHeader(item);

                return null;
            }
        });
    }

    private void prepareParametersTableView() {
        parametersTableView.setIcons(icons);

        parametersTableView.setItemFactory(new Callback<String, Item>() {
            @Override
            public Item call(String s) {
                Item item = new Item();

                item.setName(s);

                return item;
            }
        });

        parametersTableView.setItemRemover(new Callback<Item, Object>() {
            @Override
            public Object call(Item item) {
                profile.removeParameter(item);

                return null;
            }
        });
    }

    @Inject
    public void setProjectSupervisor(ProjectSupervisor projectSupervisor) {
        this.projectSupervisor = projectSupervisor;
    }

    /**
     * Set project to display.
     *
     * @param project
     */
    public void setProject(Project project) {
        if (this.project != null) {
            // Unbind previous list
            profilesListView.itemsProperty().unbind();

            listenersContainer.detachAll();
        }

        // Assign new project
        this.project = project;

        // Bind with new profiles
        profilesListView.itemsProperty().bind(project.profilesProperty());

        listenersContainer.attach(profilesListView.getSelectionModel().selectedItemProperty(), new ChangeListener<Profile>() {
            @Override
            public void changed(ObservableValue<? extends Profile> observable, Profile old, Profile profile) {
                loadProfile(profile);
            }
        });

        // Select profile
        profilesListView.getSelectionModel().select(project.getProfile());
    }

    /**
     * Set active profile. Profile need to be associated with active project.
     *
     * @param profile
     */
    private void loadProfile(Profile profile) {
        if (this.profile != null) {
            nameField.textProperty().unbindBidirectional(this.profile.nameProperty());
            baseURLField.textProperty().unbindBidirectional(this.profile.baseURLProperty());

            descriptionTextField.textProperty().unbindBidirectional(this.profile.descriptionProperty());

            variablesTableView.itemsProperty().unbindBidirectional(this.profile.variablesProperty());
            headersTableView.itemsProperty().unbindBidirectional(this.profile.headersProperty());
            parametersTableView.itemsProperty().unbindBidirectional(this.profile.parametersProperty());
        }

        // Set active profile
        this.profile = profile;

        // If profile is not defined, then nothing do.
        if (profile == null) {
            return;
        }

        nameField.textProperty().bindBidirectional(this.profile.nameProperty());
        baseURLField.textProperty().bindBidirectional(this.profile.baseURLProperty());

        descriptionTextField.textProperty().bindBidirectional(this.profile.descriptionProperty());

        variablesTableView.itemsProperty().bindBidirectional(this.profile.variablesProperty());
        headersTableView.itemsProperty().bindBidirectional(this.profile.headersProperty());
        parametersTableView.itemsProperty().bindBidirectional(this.profile.parametersProperty());
    }
}
