package com.httpio.http.impl;

import com.httpio.http.Body;
import com.httpio.http.Header;
import com.httpio.http.Method;
import com.httpio.http.Request;

import java.util.ArrayList;
import java.util.List;

public class StandardRequest implements Request {
    private String url;
    private Method method;
    private ArrayList<Header> headers = new ArrayList<>();
    private Body body;

    public void setMethod(Method method) {
        this.method = method;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    public void addHeader(Header header) {
        headers.add(header);
    }

    public void removeHeader(Header header) {
        headers.remove(header);
    }

    @Override
    public List<Header> getHeaders() {
        return headers;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    @Override
    public Body getBody() {
        return body;
    }
}
