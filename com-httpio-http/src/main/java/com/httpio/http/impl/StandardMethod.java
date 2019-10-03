package com.httpio.http.impl;

import com.httpio.http.Method;

public class StandardMethod implements Method {
    private String method;

    public StandardMethod(String method) {
        this.method = method;
    }

    @Override
    public String getId() {
        return method;
    }

    @Override
    public String getHTTPValue() {
        return method.toUpperCase();
    }

    @Override
    public String toString() {
        return getHTTPValue();
    }
}
