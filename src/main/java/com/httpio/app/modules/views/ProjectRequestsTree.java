package com.httpio.app.modules.views;

import com.google.inject.Inject;
import com.httpio.app.models.Project;
import com.httpio.app.models.Request;
import com.httpio.app.modules.controls.RequestLabel;
import com.httpio.app.services.Icons;
import com.httpio.app.services.ProjectSupervisor;
import com.httpio.app.services.RequestsCreator;
import com.httpio.app.services.Windows;
import com.httpio.app.services.Windows.Window;
import com.httpio.app.util.NodeHelper;
import com.httpio.app.util.TreeItemHelper;
import com.httpio.app.views.CreateFromRAW;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.util.Optional;

import static com.httpio.app.modules.views.ProjectRequestsTree.*;

/**
 * Basic view to support the submission tree view for projects.
 */
@SuppressWarnings("ALL")
public class ProjectRequestsTree extends TreeView<ItemWrapper> {
    private Project project;
    private ProjectSupervisor projectSupervisor;
    private Icons icons;
    private Windows windows;
    private RequestsCreator requestsCreator;

    // private TreeItem<ItemWrapper> dragged;
    private TreeItem<ItemWrapper> dragged;

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
     * Constructors
     */
    public ProjectRequestsTree() {
        super();

        getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<ItemWrapper>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<ItemWrapper>> observable, TreeItem<ItemWrapper> old, TreeItem<ItemWrapper> item) {
                if (item == null) return;

                if (item.getValue() == null) return;

                // if (item.getValue().isProject()) {
                //     // if (old == null) {
                //     //     project.setRequest(null);
                //     // } else {
                //     //     project.setRequest(old.getValue().getRequest());

                //     //     // getSelectionModel().select(old);
                //     // }
                // } else {
                //     project.setRequest(item.getValue().getRequest());
                // }

                project.setRequest(item.getValue().getRequest());
            }
        });

        init();
    }

    private void init() {
        setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                TreeItem<ItemWrapper> selected = getSelectionModel().getSelectedItem();

                if (selected != null) {
                    if (event.getCode() == KeyCode.DELETE) {
                        handleDelete(selected);
                    } else if (event.getCode() == KeyCode.F2) {
                        handleRename(selected);
                    }
                }
            }
        });

        setCellFactory(new Callback<TreeView<ItemWrapper>, TreeCell<ItemWrapper>>() {
            @Override
            public TreeCell<ItemWrapper> call(TreeView<ItemWrapper> itemWrapperTreeView) {
                TreeCell<ItemWrapper> cell = new TreeCell<ItemWrapper>() {
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

                            attachContextMenu(this, item);
                        }
                    }
                };

                // Implementing drag and drop https://brianyoung.blog/2018/08/23/javafx-treeview-drag-drop/
                cell.setOnDragDetected(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (cell.getTreeItem().getValue() == null) {
                            event.consume();

                            return;
                        }

                        dragged = cell.getTreeItem();

                        // Create dragboard
                        Dragboard dragboard = cell.startDragAndDrop(TransferMode.ANY);

                        // Create content
                        ClipboardContent content = new ClipboardContent();

                        content.putString("Test");

                        dragboard.setContent(content);
                        dragboard.setDragView(cell.snapshot(null, null));

                        event.consume();
                    }
                });

                cell.setOnDragOver(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent event) {
                        // Reasign to target value.
                        TreeCell<ItemWrapper> target = cell;

                        if (target.getTreeItem() == dragged) {
                            event.consume();

                            return;
                        }

                        if (target.getTreeItem().getValue() == null) {
                            event.consume();

                            return;
                        }

                        TreeItem<ItemWrapper> targetTreeItem =  target.getTreeItem();

                        // Check if object is not child of dragging element.
                        TreeItemHelper<ItemWrapper> targetTreeItemHelper = new TreeItemHelper<>(targetTreeItem);

                        if (!targetTreeItemHelper.hasParent(dragged)) {
                            NodeHelper targetHelper = new NodeHelper(target);

                            int zone = targetHelper.getZoneY(event);

                            targetHelper.removeClass("tree-cell-drop-hint-up");
                            targetHelper.removeClass("tree-cell-drop-hint-in");
                            targetHelper.removeClass("tree-cell-drop-hint-down");

                            Boolean accept = false;

                            System.out.println("zone: " + zone);

                            if ((zone == 0 || zone == 2) && targetTreeItem.getParent() != null) {
                                accept = true;

                                if (zone == 0) {
                                    targetHelper.addClass("tree-cell-drop-hint-up");
                                } else if (zone == 2) {
                                    targetHelper.addClass("tree-cell-drop-hint-down");
                                }
                            } else {
                                accept = true;

                                targetHelper.addClass("tree-cell-drop-hint-in");
                            }

                            if (accept) {
                                event.acceptTransferModes(TransferMode.ANY);
                            }

                            target.getTreeItem().setExpanded(true);
                        }

                        event.consume();
                    }
                });

                // cell.setOnDragEntered(new EventHandler<DragEvent>() {
                //     @Override
                //     public void handle(DragEvent event) {
                //         if (event.getGestureSource() != cell && event.getDragboard().hasString()) {

                //             // cell.setBorder(new Border(new BorderStroke()));
                //         }

                //         event.consume();
                //     }
                // });

                // cell.setOnDragDone(new EventHandler<DragEvent>() {
                //     @Override
                //     public void handle(DragEvent event) {
                //         if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                //             System.out.println("drag enterned");
                //         }
                //     }
                // });

                cell.setOnDragDropped(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent event) {
                        // Reasign to target value.
                        TreeCell<ItemWrapper> target = cell;

                        NodeHelper targetHelper = new NodeHelper(target);

                        TreeItem<ItemWrapper> targetTreeItem =  target.getTreeItem();
                        TreeItem<ItemWrapper> targetTreeItemParent = targetTreeItem.getParent();

                        int zone = targetHelper.getZoneY(event);

                        if ((zone == 0 || zone == 2) && targetTreeItemParent != null) {
                            if (zone == 0) {
                                int position = targetTreeItemParent.getChildren().indexOf(targetTreeItem);

                                position--;

                                if (position < 0) {
                                    position = 0;
                                }

                                TreeItem<ItemWrapper> n = new TreeItem<>(dragged.getValue());

                                targetTreeItemParent.getChildren().add(position, n);

                                getSelectionModel().select(n);

                                if (dragged.getParent() != null) {
                                    dragged.getParent().getChildren().remove(dragged);
                                }

                                reloadTreeItemData();
                            } else if (zone == 2){
                                int position = targetTreeItemParent.getChildren().indexOf(targetTreeItem);

                                position++;

                                TreeItem<ItemWrapper> n = new TreeItem<>(dragged.getValue());

                                targetTreeItemParent.getChildren().add(position, n);

                                getSelectionModel().select(n);

                                if (dragged.getParent() != null) {
                                    dragged.getParent().getChildren().remove(dragged);
                                }

                                reloadTreeItemData();
                            }
                        } else {
                            // targetTreeItem.getChildren().add(dragged);
                            TreeItem<ItemWrapper> n = new TreeItem<>(dragged.getValue());

                            targetTreeItem.getChildren().add(n);

                            getSelectionModel().select(n);

                            if (dragged.getParent() != null) {
                                dragged.getParent().getChildren().remove(dragged);
                            }

                            reloadTreeItemData();
                        }
                    }
                });

                cell.setOnDragExited(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent event) {
                        // Check if object is not child of dragging element.
                        NodeHelper cellHelper = new NodeHelper(cell);

                        cellHelper.removeClass("tree-cell-drop-hint-up");
                        cellHelper.removeClass("tree-cell-drop-hint-in");
                        cellHelper.removeClass("tree-cell-drop-hint-down");
                    }
                });

                return cell;
            }
        });

        getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    /**
     * Attach context menu for cell.
     */
    private void attachContextMenu(TreeCell<ItemWrapper> cell, ItemWrapper item) {
        // Init menu

        // Add request
        MenuItem itemAddRequest = new MenuItem("Add request");
        MenuItem itemAddRequestFromRaw = new MenuItem("Add request from RAW");
        MenuItem itemDuplicate = new MenuItem("Duplicate");
        MenuItem itemRename = new MenuItem("Rename");
        MenuItem itemDelete = new MenuItem("Delete");

        icons.attachIcon(itemAddRequest, Icons.ICON_ADD);
        icons.attachIcon(itemAddRequestFromRaw, Icons.ICON_ADD);
        icons.attachIcon(itemDuplicate, Icons.ICON_COPY);
        icons.attachIcon(itemRename, Icons.ICON_RENAME);
        icons.attachIcon(itemDelete, Icons.ICON_REMOVE);

        itemRename.setAccelerator(new KeyCodeCombination(KeyCode.F2));
        // itemDelete.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));

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

                handleRename(treeItem);
            }
        });

        itemAddRequestFromRaw.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleAddRequestFromRAWDialog(cell, item);
            }
        });

        itemDuplicate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleCopyRequest(cell, item);
            }
        });

        itemRename.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleRename(cell.getTreeItem());
            }
        });

        itemDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleDelete(cell.getTreeItem());
            }
        });

        // Context menu
        ContextMenu contextMenu = new ContextMenu();

        contextMenu.getItems().add(itemAddRequest);
        contextMenu.getItems().add(itemAddRequestFromRaw);
        contextMenu.getItems().add(itemDuplicate);
        contextMenu.getItems().add(itemRename);

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
    public void setWindows(Windows windows) {
        this.windows = windows;
    }

    @Inject
    public void setIcons(Icons icons) {
        this.icons = icons;
    }

    @Inject
    public void setRequestsCreator(RequestsCreator requestsCreator) {
        this.requestsCreator = requestsCreator;
    }

    /**
     * Set active project.
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
     *
     * @todo Trzeba przeładoowac drzewa tak aby zachowac informacje o rozwiniętych nodach.
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

    private void reloadTreeItemData() {
        Project itemProject = getRoot().getValue().getProject();

        ObservableList<Request> requests = FXCollections.observableArrayList();

        for(TreeItem<ItemWrapper> treeItem: getRoot().getChildren()) {
            Request request = treeItem.getValue().getRequest();

            requests.add(request);

            reloadTreeItemDataForRequest(request, treeItem.getChildren());
        }

        project.setRequests(requests);
    }

    private void reloadTreeItemDataForRequest(Request parent, ObservableList<TreeItem<ItemWrapper>> childs) {
        ObservableList<Request> requests = FXCollections.observableArrayList();

        for(TreeItem<ItemWrapper> treeItem: childs) {
            Request request = treeItem.getValue().getRequest();

            requests.add(request);

            reloadTreeItemDataForRequest(request, treeItem.getChildren());
        }

        parent.setRequests(requests);
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

    private void handleRename(TreeItem<ItemWrapper> treeItem) {
        TextInputDialog dialog = new TextInputDialog(treeItem.getValue().getName());

        dialog.setTitle("Change name of request");
        dialog.setHeaderText("Please put new name of request.");

        Optional<String> result = dialog.showAndWait();

        //noinspection OptionalIsPresent
        if (result.isPresent()) {
            treeItem.getValue().setName(result.get());
        }
    }

    private void handleAddRequestFromRAWDialog(TreeCell<ItemWrapper> cell, ItemWrapper item) {
        CreateFromRAW createFromRAW = new CreateFromRAW();

        Window<CreateFromRAW> window = windows.create(createFromRAW);

        createFromRAW.messageTextProperty().setValue("Put HTTP RAW below.");

        createFromRAW.getCreateButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String raw = createFromRAW.getText();

                if (raw == null) {
                    return;
                }

                // Creating request baseon HTTP RAW
                Request created = requestsCreator.createFromHTTPRaw(raw);

                if (item.isProject()) {
                    item.getProject().addRequest(created);
                } else {
                    item.getRequest().addRequest(created);
                }

                // Add tree item
                TreeItem<ItemWrapper> treeItem = new TreeItem<>(new ItemWrapper(created));

                cell.getTreeItem().getChildren().add(treeItem);

                window.close();

                if (project != null) {
                    project.setRequest(created);
                }
            }
        });

        createFromRAW.getCancelButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                window.close();
            }
        });

        window.showAndWait();
    }

    private void handleCopyRequest(TreeCell<ItemWrapper> cell, ItemWrapper item) {
        if (item.isProject()) {
            return;
        }

        Request created = item.getRequest().duplicate();

        if (item.getRequest().getParent() != null) {
            item.getRequest().getParent().addRequest(created);
        } else {
            project.addRequest(created);
        }

        // Add tree item
        TreeItem<ItemWrapper> treeItem = new TreeItem<>(new ItemWrapper(created));

        cell.getTreeItem().getParent().getChildren().add(treeItem);

        if (project != null) {
            project.setRequest(created);
        }

        reloadTree();
    }

    private void handleDelete(TreeItem<ItemWrapper> item) {
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

                project.reloadRequestAfterRemove();
            }
        }
    }

    /**
     * A tree or a design element may be a request.
     * ItemWrapper aims to unify the interfaces for TreeView.
     */
    public static class ItemWrapper {
        Project project;
        Request request;

        @SuppressWarnings("WeakerAccess")
        public ItemWrapper(Project project) {
            this.project = project;
        }

        @SuppressWarnings("WeakerAccess")
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