package com.httpio.app.modules;

import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public interface ItemInterface<T> {
    /**
     * ID
     */
    String getId();

    StringProperty idProperty();

    void setId(String id);

    /**
     * Name
     */
    String getName();

    StringProperty nameProperty();

    void setName(String name);

    /**
     * Value
     */
    String getValue();

    StringProperty valueProperty();

    void setValue(String value);

    /**
     * Children
     */
    ArrayList<T> getChildren();

    void setChildren(ArrayList<T> children);

    void addChild(T child);

    void removeChild(T child);

    T getParent();

    void setParent(T parent);
}
