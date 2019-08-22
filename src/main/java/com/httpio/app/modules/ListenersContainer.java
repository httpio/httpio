package com.httpio.app.modules;

import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

public class ListenersContainer {
    private final String SCOPE_DEFAULT = "default";

    private HashMap<String, HashMap<Property, ChangeListener>> registered = new HashMap<>();
    private HashMap<String, HashMap<ReadOnlyProperty, ChangeListener>> registeredReadonly = new HashMap<>();

    @SuppressWarnings("unused")
    public <T extends ItemInterface> void attachToItemsList(ListProperty<T> items, ChangeListener changeListener) {
        attachToItemsList(items, changeListener, SCOPE_DEFAULT);
    }

    public <T extends ItemInterface> void attachToItemsList(ListProperty<T> items, ChangeListener changeListener, String scope) {
        // Save at register
        if (!registered.containsKey(scope)) {
            registered.put(scope, new HashMap<>());
        }

        registered.get(scope).put(items, changeListener);

        // Add listener to whole list
        items.addListener(changeListener);

        for(T item: items) {
            registered.get(scope).put(item.nameProperty(), changeListener);
            registered.get(scope).put(item.valueProperty(), changeListener);

            item.nameProperty().addListener(changeListener);
            item.valueProperty().addListener(changeListener);
        }

        items.addListener(new ListChangeListener<T>() {
            @Override
            public void onChanged(Change<? extends T> c) {
                c.next();

                if (c.getRemovedSize() > 0) {
                    for(T item: c.getRemoved()) {
                        detach(item.nameProperty());
                        detach(item.valueProperty());
                    }
                }

                if (c.getAddedSize() > 0) {
                    for(T item: c.getAddedSubList()) {
                        registered.get(scope).put(item.nameProperty(), changeListener);
                        registered.get(scope).put(item.valueProperty(), changeListener);

                        item.nameProperty().addListener(changeListener);
                        item.valueProperty().addListener(changeListener);
                    }
                }
            }
        });
    }

    /**
     * Attach listener to property and save on list.
     */
    public <T> void attach(Property<T> property, ChangeListener<T> changeListener) {
        attach(property, changeListener, SCOPE_DEFAULT);
    }

    public <T> void attach(Property<T> property, ChangeListener<T> changeListener, String scope) {
        property.addListener(changeListener);

        if (!registered.containsKey(scope)) {
            registered.put(scope, new HashMap<>());
        }

        registered.get(scope).put(property, changeListener);
    }

    /**
     * Attach listener to readonly property and save on list.
     */
    public <T> void attach(ReadOnlyProperty<T> property, ChangeListener<T> changeListener) {
        attach(property, changeListener, SCOPE_DEFAULT);
    }

    @SuppressWarnings("WeakerAccess")
    public <T> void attach(ReadOnlyProperty<T> property, ChangeListener<T> changeListener, String scope) {
        property.addListener(changeListener);

        if (!registeredReadonly.containsKey(scope)) {
            registeredReadonly.put(scope, new HashMap<>());
        }

        registeredReadonly.get(scope).put(property, changeListener);
    }

    /**
     * Remove all listeners.
     */
    public void detachAll() {
        for(Map.Entry<String, HashMap<Property, ChangeListener>> scope: registered.entrySet()) {
            for(Map.Entry<Property, ChangeListener> entry: scope.getValue().entrySet()) {
                entry.getKey().removeListener(entry.getValue());
            }
        }

        registered = new HashMap<>();

        for(Map.Entry<String, HashMap<ReadOnlyProperty, ChangeListener>> scope: registeredReadonly.entrySet()) {
            for(Map.Entry<ReadOnlyProperty, ChangeListener> entry: scope.getValue().entrySet()) {
                entry.getKey().removeListener(entry.getValue());
            }
        }

        registeredReadonly = new HashMap<>();
    }

    public void detach(String scope) {
        if (registered.containsKey(scope)) {
            for(Map.Entry<Property, ChangeListener> entry: registered.get(scope).entrySet()) {
                entry.getKey().removeListener(entry.getValue());
            }
        }

        if (registeredReadonly.containsKey(scope)) {
            for(Map.Entry<ReadOnlyProperty, ChangeListener> entry: registeredReadonly.get(scope).entrySet()) {
                entry.getKey().removeListener(entry.getValue());
            }
        }
    }

    public void detach(Property property) {
        for(Map.Entry<String, HashMap<Property, ChangeListener>> scope: registered.entrySet()) {
            for(Map.Entry<Property, ChangeListener> entry: scope.getValue().entrySet()) {

                if (entry.getKey() == property) {
                    entry.getKey().removeListener(entry.getValue());

                    registered.get(scope.getKey()).remove(entry.getKey());
                }
            }
        }

        for(Map.Entry<String, HashMap<ReadOnlyProperty, ChangeListener>> scope: registeredReadonly.entrySet()) {
            for(Map.Entry<ReadOnlyProperty, ChangeListener> entry: scope.getValue().entrySet()) {
                if (entry.getKey() == property) {
                    entry.getKey().removeListener(entry.getValue());

                    registeredReadonly.get(scope.getKey()).remove(entry.getKey());
                }
            }
        }
    }
}
