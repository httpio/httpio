package com.httpio.http.impl;

import com.httpio.http.Header;

public class StandardHeader implements Header {
    private String name;
    private String value;

    public StandardHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
