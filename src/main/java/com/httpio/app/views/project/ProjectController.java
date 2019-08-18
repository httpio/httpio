package com.httpio.app.views.project;

import com.google.inject.Inject;
import com.httpio.app.services.*;
import com.httpio.app.services.Http.Method;
import com.httpio.app.models.Profile;
import com.httpio.app.models.Project;
import com.httpio.app.models.Request;
import com.httpio.app.modules.*;
import com.httpio.app.modules.views.ProjectRequestsTree;
import com.httpio.app.modules.views.TableViewNameValue;
import com.httpio.app.services.HTTPSender.Response;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.Callback;

/**
 * Controller for project view.
 */
public class ProjectController implements ControllerInterface {
    private final String SCOPE_REQUEST = "request";
    private final String SCOPE_PROFILE = "profile";

    private Project project;
    private Request request;
    private Profile profile;
    private Http http;
    private Logger logger;
    private ProjectSupervisor projectSupervisor;

    /**
     * Listeners container
     */
    private ListenersContainer listenersContainer = new ListenersContainer();

    /**
     * Request preparator
     */
    private RequestPreparator requestPreparator;

    /**
     * HTTP sender.
     */
    private HTTPSender httpSender;

    /**
     * Listeners
     */
    private ChangeListener onAnyChange = new ChangeListener<Object>() {
        @Override
        public void changed(ObservableValue<?> observableValue, Object o, Object t1) {
            reloadRaw();
        }
    };

    private ChangeListener<Request> projectRequestPropertyChange = new ChangeListener<Request>() {
        @Override
        public void changed(ObservableValue<? extends Request> observableValue, Request old, Request request) {
            loadRequest(request);
        }
    };

    private ChangeListener<Profile> projectProfilePropertyChange = new ChangeListener<Profile>() {
        @Override
        public void changed(ObservableValue<? extends Profile> observableValue, Profile old, Profile profile) {
            loadProfile(profile);
        }
    };

    /**
     * FXML
     */
    @FXML
    private ProjectRequestsTree requestsTree;

    @FXML
    private TextField requestResourceTextField;

    @FXML
    private ComboBox<Method> requestMethod;

    @FXML
    private Button requestSendButton;

    @FXML
    private ComboBox<Profile> requestProfileComboBox;

    @FXML
    private TableViewNameValue<Item> requestHeadersTableView;

    @FXML
    private TableViewNameValue<Item> requestParametersTableView;

    @FXML
    private TextArea requestBodyTextArea;

    @FXML
    private TextArea requestRawTextArea;

    @FXML
    private TextArea responseHeaders;

    @FXML
    private TextArea responseBody;

    @FXML
    private SplitPane splitPane;

    @Inject
    public void setRequestPreparator(RequestPreparator requestPreparator) {
        this.requestPreparator = requestPreparator;
    }

    @Inject
    public void setProjectSupervisor(ProjectSupervisor projectSupervisor) {
        this.projectSupervisor = projectSupervisor;
    }

    @Inject
    public void setHttpSender(HTTPSender httpSender) {
        this.httpSender = httpSender;
    }

    @Inject
    public void setHttp(Http http) {
        this.http = http;
    }

    @Inject
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public SplitPane getSplitPane() {
        return splitPane;
    }

    @Override
    public void prepare() {
        requestSendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleSendAction();
            }
        });

        requestMethod.setItems(http.getMethods());

        requestsTree.setProjectSupervisor(projectSupervisor);

        prepareRequestProfileComboBox();
        prepareRequestHeadersTableView();
        prepareRequestParametersTableView();
    }

    private void handleSendAction() {
        try {
            Response response = httpSender.send(request, profile);

            responseHeaders.setText(response.getHeadersRaw());
            responseBody.setText(response.getBody());
        } catch (Exception e) {
            logger.log(Logger.Levels.ERROR, e.getMessage());
        }
    }

    private void prepareRequestProfileComboBox() {
        requestProfileComboBox.setCellFactory(new Callback<ListView<Profile>, ListCell<Profile>>() {
            @Override
            public ListCell<Profile> call(ListView<Profile> profile) {
                return new ListCell<Profile>() {
                    Text text = new Text();
                    Profile profileLast;

                    @Override
                    protected void updateItem(Profile profile, boolean empty) {
                        super.updateItem(profile, empty);

                        text.textProperty().unbind();

                        if (empty) {
                            setGraphic(null);
                        } else {
                            text.textProperty().bind(profile.nameProperty());

                            setGraphic(text);
                        }
                    }
                };
            }
        });
    }

    private void prepareRequestHeadersTableView() {
        requestHeadersTableView.setItemFactory(new Callback<String, Item>() {
            @Override
            public Item call(String s) {
                Item item = new Item();

                item.setName(s);

                // Attach listener
                listenersContainer.attach(item.nameProperty(), onAnyChange);
                listenersContainer.attach(item.valueProperty(), onAnyChange);

                return item;
            }
        });

        requestHeadersTableView.setItemRemover(new Callback<Item, Object>() {
            @Override
            public Object call(Item item) {
                request.removeHeader(item);

                return null;
            }
        });
    }

    private void prepareRequestParametersTableView() {
        requestParametersTableView.setItemFactory(new Callback<String, Item>() {
            @Override
            public Item call(String s) {
                Item item = new Item();

                item.setName(s);

                // Attach listener
                listenersContainer.attach(item.nameProperty(), onAnyChange);
                listenersContainer.attach(item.valueProperty(), onAnyChange);

                return item;
            }
        });

        requestParametersTableView.setItemRemover(new Callback<Item, Object>() {
            @Override
            public Object call(Item item) {
                request.removeParameter(item);

                return null;
            }
        });
    }

    /**
     * Set project to display.
     *
     * @param project
     */
    public void setProject(Project project) {
        if (this.project != null) {
            // Unbind previous project
            requestProfileComboBox.valueProperty().unbindBidirectional(this.project.profileProperty());
            requestProfileComboBox.itemsProperty().unbind();

            this.project.profileProperty().removeListener(projectProfilePropertyChange);
            this.project.requestProperty().removeListener(projectRequestPropertyChange);
        }

        // Assign new active project
        this.project = project;

        requestsTree.setProject(project);

        // Bind with new profile
        requestProfileComboBox.itemsProperty().bind(project.profilesProperty());
        requestProfileComboBox.valueProperty().bindBidirectional(project.profileProperty());

        // Load request tree.
        // requestsRequestsTreeView.setRoot(project.getRequestsRoot());
        project.requestProperty().addListener(projectRequestPropertyChange);
        project.profileProperty().addListener(projectProfilePropertyChange);

        loadProfile(project.getProfile());
        loadRequest(project.getRequest());
    }

    private void loadProfile(Profile profile) {
        if (this.profile != null) {
            listenersContainer.detach(SCOPE_PROFILE);
        }

        this.profile = profile;

        if (profile == null) {
            // If profile is not defined.
            return;
        }

        listenersContainer.attach(profile.idProperty(), onAnyChange, SCOPE_PROFILE);
        listenersContainer.attach(profile.nameProperty(), onAnyChange, SCOPE_PROFILE);
        listenersContainer.attach(profile.protocolProperty(), onAnyChange, SCOPE_PROFILE);
        listenersContainer.attach(profile.usernameProperty(), onAnyChange, SCOPE_PROFILE);
        listenersContainer.attach(profile.passwordProperty(), onAnyChange, SCOPE_PROFILE);
        listenersContainer.attach(profile.hostProperty(), onAnyChange, SCOPE_PROFILE);
        listenersContainer.attach(profile.portProperty(), onAnyChange, SCOPE_PROFILE);
        listenersContainer.attach(profile.descriptionProperty(), onAnyChange, SCOPE_PROFILE);

        listenersContainer.attachToItemsList(profile.variablesProperty(), onAnyChange, SCOPE_PROFILE);
        listenersContainer.attachToItemsList(profile.headersProperty(), onAnyChange, SCOPE_PROFILE);
        listenersContainer.attachToItemsList(profile.parametersProperty(), onAnyChange, SCOPE_PROFILE);

        reloadRaw();
    }

    /**
     * Set active request.
     *
     * @param request
     */
    private void loadRequest(Request request) {
        if (this.request != null) {
            // Unbind previous value
            // requestMethod.valueProperty().unbindBidirectional(request.methodProperty());
            // requestResourceTextField.textProperty().unbindBidirectional(request.resourceProperty());

            // requestHeadersTableView.getTableView().itemsProperty().unbind();
            // requestParametersTableView.getTableView().itemsProperty().unbind();

            // Request method
            requestMethod.valueProperty().unbindBidirectional(this.request.methodProperty());

            // Request resource
            requestResourceTextField.textProperty().unbindBidirectional(this.request.resourceProperty());

            // Header
            requestHeadersTableView.getTableView().itemsProperty().unbindBidirectional(this.request.headersProperty());

            // Parameters
            requestParametersTableView.getTableView().itemsProperty().unbindBidirectional(this.request.parametersProperty());

            requestBodyTextArea.textProperty().unbindBidirectional(this.request.bodyProperty());

            listenersContainer.detachAll();
        }

        // Attach new request
        this.request = request;

        if (request == null) {
            return;
        }

        // Request method
        requestMethod.valueProperty().bindBidirectional(request.methodProperty());

        // Request resource
        requestResourceTextField.textProperty().bindBidirectional(request.resourceProperty());

        // Header
        requestHeadersTableView.getTableView().itemsProperty().bindBidirectional(request.headersProperty());

        // Parameters
        requestParametersTableView.getTableView().itemsProperty().bindBidirectional(request.parametersProperty());

        requestBodyTextArea.textProperty().bindBidirectional(request.bodyProperty());

        // Attached to changes
        listenersContainer.attach(request.methodProperty(), onAnyChange);
        listenersContainer.attach(request.resourceProperty(), onAnyChange);
        listenersContainer.attach(request.headersProperty(), onAnyChange);
        listenersContainer.attach(request.parametersProperty(), onAnyChange);
        listenersContainer.attach(request.bodyProperty(), onAnyChange);

        listenersContainer.attach(request.headersProperty(), onAnyChange);
        listenersContainer.attach(request.parametersProperty(), onAnyChange);

        for(Item item: request.getHeaders()) {
            listenersContainer.attach(item.nameProperty(), onAnyChange);
            listenersContainer.attach(item.valueProperty(), onAnyChange);
        }

        for(Item item: request.getParameters()) {
            listenersContainer.attach(item.nameProperty(), onAnyChange);
            listenersContainer.attach(item.valueProperty(), onAnyChange);
        }

        reloadRaw();
    }

    private void reloadRaw() {
        if (profile == null || request == null) {
            requestRawTextArea.setText("");
        } else {
            requestRawTextArea.setText(requestPreparator.prepare(request, profile).toRaw());
        }
    }
}
