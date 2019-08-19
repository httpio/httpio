package com.httpio.app.modules.views;

import com.google.inject.Inject;
import com.httpio.app.models.Project;
import com.httpio.app.models.Request;
import com.httpio.app.modules.controls.RequestLabel;
import com.httpio.app.services.Icons;
import com.httpio.app.services.ProjectSupervisor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.util.Optional;

import static com.httpio.app.modules.views.ProjectRequestsTree.*;

/**
 * Basic view to support the submission tree view for projects.
 */
public class ProjectRequestsTree extends TreeView<ItemWrapper> {
    private Project project;

    private ProjectSupervisor projectSupervisor;
    private Icons icons;

    /**
     * Listeners
     */
    private ChangeListener<Request> projectRequestChange = new ChangeListener<Request>() {
        @Override
        public void changed(ObservableValue<? extends Request> observableValue, Request old, Request request) {
            getSelectionModel().select(findTreeItemForRequest(request));
        }
    };

    /**
     * Init request tree.
     */
    public ProjectRequestsTree() {
        super();

        getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<ItemWrapper>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<ItemWrapper>> observable, TreeItem<ItemWrapper> old, TreeItem<ItemWrapper> item) {
                if (item == null) return;

                if (item.getValue() == null) return;

                if (item.getValue().isProject()) {
                    // if (old == null) {
                    //     project.setRequest(null);
                    // } else {
                    //     project.setRequest(old.getValue().getRequest());

                    //     // getSelectionModel().select(old);
                    // }
                } else {
                    project.setRequest(item.getValue().getRequest());
                }
            }
        });

        init();
    }

    private void init() {
        setCellFactory(new Callback<TreeView<ItemWrapper>, TreeCell<ItemWrapper>>() {
            @Override
            public TreeCell<ItemWrapper> call(TreeView<ItemWrapper> itemWrapperTreeView) {
                return new TreeCell<ItemWrapper>() {
                    Text text = new Text();
                    RequestLabel label = new RequestLabel();

                    @Override
                    protected void updateItem(ItemWrapper item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                        } else {
                            if (item.isProject()) {
                                text.textProperty().bind(item.nameProperty());

                                setGraphic(text);
                            } else {
                                label.requestProperty().setValue(item.getRequest());

                                setGraphic(label);
                            }
                        }

                        attachContextMenu(this, item);
                    }
                };
            }
        });

        getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    /**
     * Attachs context menu for cell
     *
     * @param cell
     * @param item
     */
    private void attachContextMenu(TreeCell<ItemWrapper> cell, ItemWrapper item) {
        // Init menu
        MenuItem itemAddRequest = new MenuItem("Add request");

        itemAddRequest.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Request created = projectSupervisor.createNewRequest();

                if (item.isProject()) {
                    item.getProject().addRequest(created);
                } else {
                    item.getRequest().addRequest(created);
                }

                // Add tree item
                TreeItem<ItemWrapper> treeItem = new TreeItem<>(new ItemWrapper(created));

                cell.getTreeItem().getChildren().add(treeItem);

                showRenameDialog(treeItem);
            }
        });

        icons.attachIcon(itemAddRequest, Icons.ICON_ADD);

        MenuItem itemRename = new MenuItem("Rename");

        itemRename.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showRenameDialog(cell.getTreeItem());
            }
        });

        icons.attachIcon(itemRename, Icons.ICON_RENAME);

        MenuItem itemDelete = new MenuItem("Delete");

        itemDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showDeleteDialog(cell.getTreeItem());
            }
        });

        icons.attachIcon(itemDelete, Icons.ICON_REMOVE);

        ContextMenu contextMenu = new ContextMenu();

        contextMenu.getItems().add(itemAddRequest);
        contextMenu.getItems().add(itemRename);

        System.out.println("------------");
        System.out.println("size: " + project.getRequests().size());

        if (cell.getTreeItem() != getRoot() && project.getRequests().size() > 1) {
            contextMenu.getItems().add(itemDelete);
        }

        cell.setContextMenu(contextMenu);
    }

    @Inject
    public void setProjectSupervisor(ProjectSupervisor projectSupervisor) {
        this.projectSupervisor = projectSupervisor;
    }

    @Inject
    public void setIcons(Icons icons) {
        this.icons = icons;
    }

    /**
     * Set active project.
     *
     * @param project
     */
    public void setProject(Project project) {
        if (this.project != null) {
            this.project.requestProperty().removeListener(projectRequestChange);
        }

        this.project = project;

        if (project == null) {
            return;
        }

        project.requestProperty().addListener(projectRequestChange);

        reloadTree();
    }

    /**
     * Reload tree.
     */
    private void reloadTree() {
        // Root is project
        TreeItem<ItemWrapper> treeItemRoot = new TreeItem<>(new ItemWrapper(project));

        treeItemRoot.setExpanded(true);

        // Create tree.
        setItemsFor(treeItemRoot, project.getRequests());

        // Set root
        setRoot(treeItemRoot);
    }

    private void setItemsFor(TreeItem<ItemWrapper> parent, ObservableList<Request> children) {
        for(Request child: children) {
            TreeItem<ItemWrapper> item = new TreeItem<>(new ItemWrapper(child));

            parent.getChildren().add(item);

            setItemsFor(item, child.getRequests());
        }
    }

    private TreeItem<ItemWrapper> findTreeItemForRequest(Request item) {
        return findTreeItemForRequestResursive(item, getRoot().getChildren());
    }

    private TreeItem<ItemWrapper> findTreeItemForRequestResursive(Request item, ObservableList<TreeItem<ItemWrapper>> children) {
        TreeItem<ItemWrapper> found = null;

        for(TreeItem<ItemWrapper> treeItem : children) {
            if (treeItem.getValue().getRequest() == item) {
                found = treeItem;

                break;
            }
        }

        if (found != null) {
            return found;
        } else {
            for(TreeItem<ItemWrapper> treeItem : children) {
                found = findTreeItemForRequestResursive(item, treeItem.getChildren());

                if (found != null) {
                    break;
                }
            }
        }

        return found;
    }

    private void showRenameDialog(TreeItem<ItemWrapper> treeItem) {
        TextInputDialog dialog = new TextInputDialog(treeItem.getValue().getName());

        dialog.setTitle("Change name of request");
        dialog.setHeaderText("Please put new name of request.");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            treeItem.getValue().setName(result.get());
        }
    }

    private void showDeleteDialog(TreeItem<ItemWrapper> item) {
        if (item.getValue().isProject()) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + item.getValue().getName() + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            // Remove item from tree
            item.getParent().getChildren().remove(item);

            // Remove item from requests
            Request request = item.getValue().getRequest();

            if (request.getParent() != null) {
                request.getParent().removeRequest(request);

                // Set parent as active request.
                project.setRequest(request.getParent());
            } else {
                project.removeRequest(request);

                project.setRequestAfterRemove();
            }
        }
    }

    /**
     * A tree or a design element may be a request. ItemWrapper aims to unify the interfaces for TreeView.
     */
    public static class ItemWrapper {
        Project project;
        Request request;

        public ItemWrapper(Project project) {
            this.project = project;
        }

        public ItemWrapper(Request request) {
            this.request = request;
        }

        public Project getProject() {
            return project;
        }

        public Request getRequest() {
            return request;
        }

        public boolean isProject() {
            return project != null;
        }

        public String getName() {
            if (project != null) {
                return project.getName();
            } else if (request != null) {
                return request.getName();
            } else {
                return null;
            }
        }

        public SimpleStringProperty nameProperty() {
            if (project != null) {
                return project.nameProperty();
            } else if (request != null) {
                return request.nameProperty();
            } else {
                return null;
            }
        }

        public void setName(String name) {
            if (project != null) {
                project.setName(name);
            } else if (request != null) {
                request.setName(name);
            }
        }
    }
}