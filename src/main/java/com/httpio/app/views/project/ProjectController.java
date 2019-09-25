package com.httpio.app.views.project;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.httpio.app.modules.views.JsonTreeView;
import com.httpio.app.services.*;
import com.httpio.app.services.Http.Method;
import com.httpio.app.models.Profile;
import com.httpio.app.models.Project;
import com.httpio.app.models.Request;
import com.httpio.app.modules.*;
import com.httpio.app.modules.views.ProjectRequestsTree;
import com.httpio.app.modules.views.TableViewNameValue;
import com.httpio.app.services.HTTPSender.Response;
import com.httpio.app.services.Windows.Window;
import com.httpio.app.tasks.ExecuteRequest;
import com.httpio.app.util.GenericWrapper;
import com.httpio.app.util.NodeHelper;
import com.httpio.app.views.profiles.ProfilesView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.controlsfx.control.BreadCrumbBar;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;

/**
 * Controller for project view.
 */
public class ProjectController implements ControllerInterface {
    private final String SCOPE_PROJECT = "Project";
    private final String SCOPE_REQUEST = "Request";
    private final String SCOPE_PROFILE = "Profile";

    private Injector injector;
    private Project project;
    private Request request;
    private Profile profile;
    private Http http;
    private Icons icons;
    private ProjectSupervisor projectSupervisor;
    private Windows windows;
    private RequestsCreator requestsCreator;
    private TasksSupervisor tasksSupervisor;
    private Callback<TreeItem<GenericWrapper>, Button> breadCrumbBarDefaultCrumbNodeFactory;

    /**
     * Listeners container
     */
    private ListenersContainer listenersContainer = new ListenersContainer();

    /**
     * Request preparator
     */
    private HTTPRequestPreparator httpRequestPreparator;

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
    private TextField urlField;

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

    @FXML
    private JsonTreeView jsonTreeField;

    @FXML
    private Button profilesButton;

    @FXML
    private BreadCrumbBar<GenericWrapper> breadCrumbBar;

    @Inject
    public void setHttpRequestPreparator(HTTPRequestPreparator httpRequestPreparator) {
        this.httpRequestPreparator = httpRequestPreparator;
    }

    @Inject
    public void setProjectSupervisor(ProjectSupervisor projectSupervisor) {
        this.projectSupervisor = projectSupervisor;
    }

    @Inject
    public void setTasksSupervisor(TasksSupervisor tasksSupervisor) {
        this.tasksSupervisor = tasksSupervisor;
    }

    @Inject
    public void setRequestsCreator(RequestsCreator requestsCreator) {
        this.requestsCreator = requestsCreator;
    }

    @Inject
    public void setIcons(Icons icons) {
        this.icons = icons;
    }

    @Inject
    public void setWindows(Windows windows) {
        this.windows = windows;
    }

    @Inject
    public void setHttpSender(HTTPSender httpSender) {
        this.httpSender = httpSender;
    }

    @Inject
    public void setInjector(Injector injector) {
        this.injector = injector;
    }

    @Inject
    public void setHttp(Http http) {
        this.http = http;
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
        requestsTree.setIcons(icons);
        requestsTree.setWindows(windows);
        requestsTree.setRequestsCreator(requestsCreator);

        prepareRequestProfileComboBox();
        prepareRequestHeadersTableView();
        prepareRequestParametersTableView();

        prepareProfilesButton();
        prepareBreadCrumbBar();
    }

    private void prepareBreadCrumbBar() {
        breadCrumbBarDefaultCrumbNodeFactory = breadCrumbBar.getCrumbFactory();

        breadCrumbBar.setCrumbFactory(new Callback<TreeItem<GenericWrapper>, Button>() {
            @Override
            public Button call(TreeItem<GenericWrapper> treeItem) {
                Button button = breadCrumbBarDefaultCrumbNodeFactory.call(treeItem);
                final GenericWrapper item = treeItem.getValue();

                (new NodeHelper(button)).addClass("httpio-bread-crum-bar__node");

                button.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (item.isProject()) {
                            // button.textProperty().bind(item.getProject().nameProperty());
                        } else if (item.isRequest()) {
                            projectSupervisor.getProject().setRequest(item.getRequest());
                        } else {
                            // TODO Log bledow
                        }
                    }
                });

                if (item.isProject()) {
                    button.textProperty().bind(item.getProject().nameProperty());
                } else if (item.isRequest()) {
                    button.textProperty().bind(item.getRequest().nameProperty());
                } else {
                    // TODO Log bledow
                }

                return button;
            }
        });

        breadCrumbBar.selectedCrumbProperty().addListener(new ChangeListener<TreeItem<GenericWrapper>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<GenericWrapper>> observable, TreeItem<GenericWrapper> previous, TreeItem<GenericWrapper> treeItem) {
                GenericWrapper item = treeItem.getValue();

                if (item.isProject()) {

                } else if (item.isRequest()) {
                    projectSupervisor.getProject().setRequest(item.getRequest());
                }
            }
        });
    }

    private void prepareProfilesButton() {
        profilesButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    ProfilesView profilesView = new ProfilesView(injector, projectSupervisor.getProject());

                    profilesView.load();

                    Window window = windows.create(profilesView.getView());

                    window.setTitle("Httpio - profiles settings");
                    window.showAndWait();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
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
        requestHeadersTableView.setIcons(icons);

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
        requestParametersTableView.setIcons(icons);

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

    private void handleSendAction() {
        tasksSupervisor.start(new ExecuteRequest(request, profile, httpSender));
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

            // Remove all listeners from previous project.
            listenersContainer.detach(SCOPE_PROJECT);
        }

        // Assign new active project
        this.project = project;

        requestsTree.setProject(project);

        // Bind with new profile
        requestProfileComboBox.itemsProperty().bind(project.profilesProperty());
        requestProfileComboBox.valueProperty().bindBidirectional(project.profileProperty());

        // Load request tree.
        // requestsRequestsTreeView.setRoot(project.getRequestsRoot());
        project.profileProperty().addListener(projectProfilePropertyChange);

        // Attach project to breadcrumb
        listenersContainer.attach(project.requestProperty(), new ChangeListener<Request>() {
            @Override
            public void changed(ObservableValue<? extends Request> observable, Request previous, Request request) {
                loadRequest(request);
            }
        }, SCOPE_PROJECT);

        attachToChangeRequestsTreeListener(project.getRequests());

        loadProfile(project.getProfile());
        loadRequest(project.getRequest());
    }

    private void attachToChangeRequestsTreeListener(ObservableList<Request> requests) {
        listenersContainer.attach(requests, new ListChangeListener<Request>() {
            @Override
            public void onChanged(Change<? extends Request> change) {
                reloadBreadCrumbBar();
            }
        }, SCOPE_PROJECT);

        for(Request request: requests) {
            listenersContainer.attach(request.parentProperty(), new ChangeListener<Request>() {
                @Override
                public void changed(ObservableValue<? extends Request> observable, Request previous, Request r) {
                    reloadBreadCrumbBar();
                }
            }, SCOPE_PROJECT);

            attachToChangeRequestsTreeListener(request.getRequests());
        }
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
        listenersContainer.attach(profile.baseURLProperty(), onAnyChange, SCOPE_PROFILE);
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
            // Request method
            requestMethod.valueProperty().unbindBidirectional(this.request.methodProperty());

            // Request resource
            urlField.textProperty().unbindBidirectional(this.request.urlProperty());

            // Header
            requestHeadersTableView.getTableView().itemsProperty().unbindBidirectional(this.request.headersProperty());

            // Parameters
            requestParametersTableView.getTableView().itemsProperty().unbindBidirectional(this.request.parametersProperty());

            requestBodyTextArea.textProperty().unbindBidirectional(this.request.bodyProperty());

            listenersContainer.detach(SCOPE_REQUEST);
        }

        // Attach new request
        this.request = request;

        if (request == null) {
            return;
        }

        requestMethod.valueProperty().bindBidirectional(request.methodProperty());
        urlField.textProperty().bindBidirectional(request.urlProperty());

        requestHeadersTableView.getTableView().itemsProperty().bindBidirectional(request.headersProperty());
        requestParametersTableView.getTableView().itemsProperty().bindBidirectional(request.parametersProperty());
        requestBodyTextArea.textProperty().bindBidirectional(request.bodyProperty());

        // Attached to changes
        listenersContainer.attach(request.methodProperty(), onAnyChange, SCOPE_REQUEST);
        listenersContainer.attach(request.urlProperty(), onAnyChange, SCOPE_REQUEST);
        listenersContainer.attach(request.headersProperty(), onAnyChange, SCOPE_REQUEST);
        listenersContainer.attach(request.parametersProperty(), onAnyChange, SCOPE_REQUEST);
        listenersContainer.attach(request.bodyProperty(), onAnyChange, SCOPE_REQUEST);

        listenersContainer.attach(request.headersProperty(), onAnyChange, SCOPE_REQUEST);
        listenersContainer.attach(request.parametersProperty(), onAnyChange, SCOPE_REQUEST);

        for(Item item: request.getHeaders()) {
            listenersContainer.attach(item.nameProperty(), onAnyChange, SCOPE_REQUEST);
            listenersContainer.attach(item.valueProperty(), onAnyChange, SCOPE_REQUEST);
        }

        for(Item item: request.getParameters()) {
            listenersContainer.attach(item.nameProperty(), onAnyChange);
            listenersContainer.attach(item.valueProperty(), onAnyChange);
        }

        // Load last response
        listenersContainer.attach(request.lastResponseProperty(), new ChangeListener<Response>() {
            @Override
            public void changed(ObservableValue<? extends Response> observable, Response old, Response value) {
                reloadResponse();
            }
        }, SCOPE_REQUEST);

        reloadResponse();
        reloadRaw();
        reloadBreadCrumbBar();
    }

    private void reloadResponse() {
        responseHeaders.setText(null);
        responseBody.setText(null);

        if (request == null) {
            return;
        }

        Response lastResponse = request.getLastResponse();

        if (lastResponse == null) {
            return;
        }

        responseHeaders.setText(lastResponse.getHeadersRaw());
        responseBody.setText(lastResponse.getBody());

        jsonTreeField.setInput(lastResponse.getBody());
    }

    private void reloadRaw() {
        if (profile == null || request == null) {
            requestRawTextArea.setText("");
        } else {
            try {
                requestRawTextArea.setText(httpRequestPreparator.prepare(request, profile).toRaw());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    private void reloadBreadCrumbBar() {
        TreeItem<GenericWrapper> treeItemRoot = new TreeItem<>(new GenericWrapper(project));
        TreeItem<GenericWrapper> parent = treeItemRoot;
        TreeItem<GenericWrapper> last = treeItemRoot;

        if (project.getRequest() != null) {
            List<Request> path = project.getRequest().getRequestsFlatPathToRoot();

            Collections.reverse(path);

            for(Request request: path) {
                System.out.print(request.getName() + " - ");

                TreeItem<GenericWrapper> treeItem = new TreeItem<>(new GenericWrapper(request));

                last = treeItem;

                parent.getChildren().add(treeItem);

                parent = treeItem;
            }
        }

        breadCrumbBar.setSelectedCrumb(last);
        breadCrumbBar.setAutoNavigationEnabled(false);
    }
}
