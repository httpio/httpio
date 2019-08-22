package com.httpio.app.modules;

import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;

import java.util.HashMap;
import java.util.Map;

public class ListenersContainer {
    private final String SCOPE_DEFAULT = "default";

    private HashMap<String, HashMap<Property, ChangeListener>> registered = new HashMap<>();
    private HashMap<String, HashMap<ReadOnlyProperty, ChangeListener>> registeredReadonly = new HashMap<>();

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

    @SuppressWarnings("unused")
    public void attachToItemsList(ListProperty<? extends Item> items, ChangeListener changeListener) {
        attachToItemsList(items, changeListener, SCOPE_DEFAULT);
    }

    public void attachToItemsList(ListProperty<? extends Item> items, ChangeListener changeListener, String scope) {
        attach(items, changeListener, scope);

        for(Item item: items) {
            attach(item.valueProperty(), changeListener, scope);
            attach(item.nameProperty(), changeListener, scope);
        }
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
}
