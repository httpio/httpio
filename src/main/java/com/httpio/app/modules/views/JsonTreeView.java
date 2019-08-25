package com.httpio.app.modules.views;

import com.google.gson.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Map;

/**
 * Json tree view.
 *
 * Thanks for fabian.
 *
 * https://stackoverflow.com/questions/51039132/read-arbitrarily-json-data-to-a-javafx-treeview-and-only-show-the-first-element
 */
public class JsonTreeView extends VBox {
    private StringProperty input = new SimpleStringProperty();

    @FXML
    private TreeView<Value> treeField;

    @FXML
    private Text messageField;

    private static final String INPUT = "{\n"
        + "    name:\"tom\",\n"
        + "    schools:[\n"
        + "        {\n"
        + "            name:\"school1\",\n"
        + "            tags:[\"maths\",\"english\"]\n"
        + "        },\n"
        + "        {\n"
        + "            name:\"school2\",\n"
        + "            tags:[\"english\",\"biological\"]\n"
        + "        },\n"
        + "    ]\n"
        + "}"
    ;

    private static final Image JSON_IMAGE = new Image("https://i.stack.imgur.com/1slrh.png");

    public JsonTreeView() {
        super();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("JsonTreeView.fxml"));

            loader.setController(this);
            loader.setRoot(this);
            loader.load();

        } catch (IOException exc) {
            exc.printStackTrace();
        }

        init();
    }

    private void init() {
        input.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String old, String input) {
                reloadTreeView();
            }
        });

        treeField.setCellFactory(tv -> new TreeCell<Value>() {
            private final ImageView imageView;

            // Constructor
            {
                imageView = new ImageView(JSON_IMAGE);
                imageView.setFitHeight(18);
                imageView.setFitWidth(16);
                imageView.setPreserveRatio(true);

                setGraphic(imageView);
            }

            @Override
            protected void updateItem(Value item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText("");
                    imageView.setVisible(false);
                } else {
                    setText(item.text);
                    imageView.setVisible(true);
                    imageView.setViewport(item.type.viewport);
                }
            }
        });

        reloadTreeView();
    }

    private void reloadTreeView() {
        JsonParser parser = new JsonParser();

        if (input.getValue() != null) {
            try {
                JsonElement root = parser.parse(input.getValue());

                treeField.setRoot(createTree(root));

                // ...
            } catch (JsonSyntaxException e) {
                // ...
            }

        } else {
            // ...
        }
    }

    private static TreeItem<Value> createTree(JsonElement element) {
        if (element.isJsonNull()) {
            return new TreeItem<>(new Value("null", Type.PROPERTY));
        } else if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();

            return new TreeItem<>(new Value(primitive.isString() ? '"' + primitive.getAsString() + '"' : primitive.getAsString(), Type.PROPERTY));
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();

            TreeItem<Value> item = new TreeItem<>(new Value(Type.ARRAY));

            // for (int i = 0, max = Math.min(1, array.size()); i < max; i++) {
            for (int i = 0, max = array.size(); i < max; i++) {
                TreeItem<Value> child = createTree(array.get(i));

                prependString(child, Integer.toString(i));

                item.getChildren().add(child);
            }

            return item;
        } else {
            JsonObject object = element.getAsJsonObject();

            TreeItem<Value> item = new TreeItem<>(new Value(Type.OBJECT));

            for (Map.Entry<String, JsonElement> property : object.entrySet()) {
                TreeItem<Value> child = createTree(property.getValue());
                prependString(child, property.getKey());
                item.getChildren().add(child);
            }

            return item;
        }
    }

    private static void prependString(TreeItem<Value> item, String string) {
        String val = item.getValue().text;

        item.getValue().text = (val == null ? string : string + " : " + val);
    }

    public String getInput() {
        return input.get();
    }

    public StringProperty inputProperty() {
        return input;
    }

    public void setInput(String input) {
        this.input.set(input);
    }

    /**
     *
     */
    private enum Type {
        OBJECT(new Rectangle2D(45, 52, 16, 18)),
        ARRAY(new Rectangle2D(61, 88, 16, 18)),
        PROPERTY(new Rectangle2D(31, 13, 16, 18));

        private final Rectangle2D viewport;

        private Type(Rectangle2D viewport) {
            this.viewport = viewport;
        }

    }

    /**
     *
     */
    private static final class Value {
        private String text;
        private final Type type;

        public Value(Type type) {
            this.type = type;
        }

        public Value(String text, Type type) {
            this.text = text;
            this.type = type;
        }
    }
}
