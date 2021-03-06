package com.httpio.app.modules.views;

import com.google.inject.Inject;
import com.httpio.app.modules.ItemInterface;
import com.httpio.app.services.Icons;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings({"FieldCanBeLocal", "Convert2Lambda"})
public class TableViewNameValue<T extends ItemInterface> extends VBox implements Initializable {
    private TableColumn<T, String> columnName;
    private TableColumn<T, String> columnValue;
    private TableColumn<T, String> columnActions;

    private Icons icons;
    private Boolean userInputParser = true;

    /**
     * Factories
     */
    private Callback<String, T> itemFactory;
    private Callback<T, ?> itemRemover;

    /**
     * Fields
     */
    @FXML
    @SuppressWarnings("unused")
    private TableView<T> tableView;

    @FXML
    @SuppressWarnings("unused")
    private TextField addTextField;

    @FXML
    @SuppressWarnings("unused")
    private Button addButton;

    /**
     * Constructors
     */
    public TableViewNameValue() {
        super();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TableViewNameValue.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();

        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleAddItem();
            }
        });

        addTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    handleAddItem();
                }
            }
        });

        initializeTableView();
    }

    private void handleAddItem() {
        String v = addTextField.getText();

        if (!v.equals("") && itemFactory != null) {
            T item = itemFactory.call(v);

            if (userInputParser && v.contains(":")) {
                String[] parts = v.split(":");

                item.setName(parts[0].trim());

                if (parts.length == 2) {
                    item.setValue(parts[1].trim());
                }
            }

            tableView.getItems().add(item);

            addTextField.setText("");
        }
    }

    private void initializeTableView() {
        tableView.setEditable(true);

        // Value name
        columnName = new TableColumn<>("Name");
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));

        columnName.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<T, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<T, String> event) {
                int index = event.getTablePosition().getRow();
                T item = event.getTableView().getItems().get(index);

                item.setName(event.getNewValue());
            }
        });

        columnName.setCellFactory(TextFieldTableCell.forTableColumn());

        // Value column
        columnValue = new TableColumn<>("Value");
        columnValue.setCellValueFactory(new PropertyValueFactory<>("value"));

        columnValue.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<T, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<T, String> event) {
                int index = event.getTablePosition().getRow();
                T item = event.getTableView().getItems().get(index);

                item.setValue(event.getNewValue());
            }
        });

        columnValue.setCellFactory(TextFieldTableCell.forTableColumn());

        columnActions = new TableColumn<>();
        columnActions.setMaxWidth(200);

        columnActions.setCellFactory(new Callback<TableColumn<T, String>, TableCell<T, String>>() {
            @Override
            public TableCell<T, String> call(TableColumn<T, String> param) {
                TableCell<T, String> cell = new TableCell<T, String>() {
                    private Button remove = new Button();

                    @Override
                    protected void updateItem(String value, boolean empty) {
                        super.updateItem(value, empty);

                        if (getTableRow() == null) {
                            setGraphic(null);

                            return;
                        }

                        if (getTableRow().getItem() == null) {
                            setGraphic(null);

                            return;
                        }

                        // Set actions
                        T item = (T) getTableRow().getItem();

                        icons.attachIcon(remove, Icons.ICON_REMOVE);

                        remove.getStyleClass().clear();

                        remove.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                if (itemRemover != null) itemRemover.call(item);
                            }
                        });

                        setGraphic(remove);
                    }
                };

                cell.setAlignment(Pos.CENTER);

                return cell;
            }
        });

        tableView.getColumns().add(columnName);
        tableView.getColumns().add(columnValue);
        tableView.getColumns().add(columnActions);
    }

    @Inject
    public void setIcons(Icons icons) {
        this.icons = icons;
    }

    /**
     * Set item factory. If new alement is added then string form input is given to callback.
     * Callback needs to be returns new object of <T> type.
     */
    public void setItemFactory(Callback<String, T> itemFactory) {
        this.itemFactory = itemFactory;
    }

    public void setItemRemover(Callback<T, ?> itemRemover) {
        this.itemRemover = itemRemover;
    }

    @SuppressWarnings("unused")
    public ObservableList<T> getItems() {
        return tableView.getItems();
    }

    @SuppressWarnings("unused")
    public void setItems(ObservableList<T> items) {
        tableView.getItems().setAll(items);
    }

    public final ObjectProperty<ObservableList<T>> itemsProperty() {
        return tableView.itemsProperty();
    }

    public TableView<T> getTableView() {
        return tableView;
    }

    /**
     * Input parser
     */
    public Boolean getUserInputParser() {
        return userInputParser;
    }

    public void setUserInputParser(Boolean userInputParser) {
        this.userInputParser = userInputParser;
    }
}
