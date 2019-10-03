package com.httpio.app.modules;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.UUID;

public class Item implements ItemInterface<Item> {
    private StringProperty id = new SimpleStringProperty();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty value = new SimpleStringProperty();

    private ArrayList<Item> children = new ArrayList<>();

    private Item parent;

    /**
     * Constructs
     */
    public Item() {
        id.setValue(UUID.randomUUID().toString());
    }

    public Item(String id) {
        this.id.setValue(id);
    }

    public Item(String id, String name, String value) {
        this.id.setValue(id);
        this.name.setValue(name);
        this.value.setValue(value);
    }

    @Override
    public String toString() {
        return id.getValue();
    }

    @Override
    public String getId() {
        return id.getValue();
    }

    @Override
    public void setId(String id) {
        this.id.setValue(id);
    }

    public StringProperty idProperty() {
        return id;
    }

    @Override
    public String getName() {
        return name.getValue();
    }

    @Override
    public void setName(String name) {
        this.name.setValue(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public String getValue() {
        return value.getValue();
    }

    @Override
    public void setValue(String value) {
        this.value.setValue(value);
    }

    public StringProperty valueProperty() {
        return value;
    }

    @Override
    public ArrayList<Item> getChildren() {
        return children;
    }

    @Override
    public void setChildren(ArrayList<Item> children) {
        this.children.clear();
        this.children.addAll(children);
    }

    @Override
    public void addChild(Item child) {
        children.add(child);
    }

    @Override
    public void removeChild(Item child) {
        children.remove(child);
    }

    @Override
    public Item getParent() {
        return parent;
    }

    @Override
    public void setParent(Item parent) {
        this.parent = parent;
    }
}
