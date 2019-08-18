package com.httpio.app.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Http {
    ObservableList<Method> methods = FXCollections.observableArrayList();
    ObservableList<Protocol> protocols = FXCollections.observableArrayList();

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

        // Init protocols
        protocols.add(new Protocol(Protocols.HTTP, "HTTP"));
        protocols.add(new Protocol(Protocols.HTTPS, "HTTPS"));
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

    public ObservableList<Protocol> getProtocols() {
        return protocols;
    }

    public Protocol getProtocolById(Protocols protocol) {
        for(Protocol m: protocols) {
            if (m.getId() == protocol) {
                return m;
            }
        }

        return null;
    }

    public Protocol getProtocolById(String protocol) {
        for(Protocol m: protocols) {
            if (m.getId().toString().equals(protocol)) {
                return m;
            }
        }

        return null;
    }

    /**
     * Protocols
     */
    public enum Protocols {
        HTTP,
        HTTPS
    }

    public class Protocol {
        Protocols id;
        String name;

        public Protocol(Protocols id, String name) {
            this.id = id;
            this.name = name;
        }

        public Protocols getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
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
