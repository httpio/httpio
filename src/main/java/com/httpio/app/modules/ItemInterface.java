package com.httpio.app.modules;

import java.util.ArrayList;

public interface ItemInterface<T> {
    String getId();

    void setId(String id);

    String getName();

    void setName(String name);

    String getValue();

    void setValue(String value);

    ArrayList<T> getChildren();

    void setChildren(ArrayList<T> children);

    void addChild(T child);

    void removeChild(T child);

    T getParent();

    void setParent(T parent);
}
