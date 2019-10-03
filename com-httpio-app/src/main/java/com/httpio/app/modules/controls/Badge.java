package com.httpio.app.modules.controls;

import com.httpio.app.util.FX;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;

// <p class="text-primary">.text-primary</p>
// <p class="text-secondary">.text-secondary</p>
// <p class="text-success">.text-success</p>
// <p class="text-danger">.text-danger</p>
// <p class="text-warning">.text-warning</p>
// <p class="text-info">.text-info</p>
// <p class="text-light bg-dark">.text-light</p>
// <p class="text-dark">.text-dark</p>
// <p class="text-muted">.text-muted</p>
// <p class="text-white bg-dark">.text-white</p>
public class Badge extends HBox {

    private ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.SILVER);

    @FXML
    Text text;

    public Badge() {
        super();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Badge.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();

        } catch (IOException exc) {
            exc.printStackTrace();
        }

        init();
    }

    private void init() {
        color.addListener(new ChangeListener<Color>() {
            @Override
            public void changed(ObservableValue<? extends Color> observableValue, Color color, Color t1) {
                reload();
            }
        });

        reload();
    }

    private void reload() {
        FX.setStyle(this, "-fx-background-color", FX.toRGBCode(color.getValue()));
        FX.setStyle(text, "-fx-fill", FX.toRGBCode(color.getValue().invert()));
    }

    public void setText(String text) {
        this.text.textProperty().setValue(text);
    }

    public StringProperty textProperty() {
        return text.textProperty();
    }

    /**
     * Color
     */
    public Color getColor() {
        return color.get();
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    public void setColor(Color color) {
        this.color.set(color);
    }
}
