package com.httpio.app.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

public class Http {
    private ObservableList<Method> methods = FXCollections.observableArrayList();

    public Http() {
        // Init methods
        methods.add(new Method(Methods.GET, "GET"));
        methods.add(new Method(Methods.HEAD, "HEAD"));
        methods.add(new Method(Methods.PUT, "PUT"));
        methods.add(new Method(Methods.POST, "POST"));
        methods.add(new Method(Methods.DELETE, "DELETE"));
        methods.add(new Method(Methods.OPTIONS, "OPTIONS"));
        methods.add(new Method(Methods.TRACE, "TRACE"));
        methods.add(new Method(Methods.CONNECT, "CONNECT"));
        methods.add(new Method(Methods.PATCH, "PATCH"));
    }

    public ObservableList<Method> getMethods() {
        return methods;
    }

    public Method getMethodById(Methods method) {
        for(Method m: methods) {
            if (m.getId() == method) {
                return m;
            }
        }

        return null;
    }

    public Method getMethodById(String method) {
        for(Method m: methods) {
            if (m.getId().toString().equals(method)) {
                return m;
            }
        }

        return null;
    }

    /**
     * Methods
     */
    public enum Methods {
        GET,
        HEAD,
        PUT,
        POST,
        DELETE,
        OPTIONS,
        TRACE,
        CONNECT,
        PATCH
    }

    public class Method {
        Methods id;
        String name;

        public Method(Methods id, String name) {
            this.id = id;
            this.name = name;
        }

        public Methods getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }

        public Color getColor() {
            if (id == Methods.GET) {
                return Color.LIGHTBLUE;
            } else if (id == Methods.HEAD) {
                return Color.SILVER;
            } else if (id == Methods.PUT) {
                return Color.ORANGE;
            } else if (id == Methods.POST) {
                return Color.LIGHTGREEN;
            } else if (id == Methods.DELETE) {
                return Color.CRIMSON;
            } else if (id == Methods.OPTIONS) {
                return Color.SILVER;
            } else if (id == Methods.TRACE) {
                return Color.SILVER;
            } else if (id == Methods.CONNECT) {
                return Color.SILVER;
            } else if (id == Methods.PATCH) {
                return Color.AZURE;
            } else {
                return null;
            }
        }

        public String toHTTPHeader() {
            if (id == Methods.GET) {
                return "GET";
            } else if (id == Methods.HEAD) {
                return "HEAD";
            } else if (id == Methods.PUT) {
                return "PUT";
            } else if (id == Methods.POST) {
                return "POST";
            } else if (id == Methods.DELETE) {
                return "DELETE";
            } else if (id == Methods.OPTIONS) {
                return "OPTIONS";
            } else if (id == Methods.TRACE) {
                return "TRACE";
            } else if (id == Methods.CONNECT) {
                return "CONNECT";
            } else if (id == Methods.PATCH) {
                return "PATCH";
            } else {
                return null;
            }
        }
    }
}
