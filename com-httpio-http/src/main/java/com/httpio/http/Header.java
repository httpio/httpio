package com.httpio.http;

public interface Header {

    void setName(String name);
    String getName();

    void setValue(String value);
    String getValue();
}
