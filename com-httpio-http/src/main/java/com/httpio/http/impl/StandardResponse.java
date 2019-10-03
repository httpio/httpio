package com.httpio.http.impl;

import com.httpio.http.Body;
import com.httpio.http.Code;
import com.httpio.http.Header;
import com.httpio.http.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StandardResponse implements Response {
    private Code code;
    private ArrayList<Header> headers = new ArrayList<>();
    private Body body;
    private Exception exception;

    @Override
    public void setCode(Code code) {
        this.code = code;
    }

    @Override
    public Code getCode() {
        return code;
    }

    @Override
    public void setHeaders(List<Header> headers) {
        this.headers.clear();
        this.headers.addAll(headers);
    }

    @Override
    public List<Header> getHeaders() {
        return headers;
    }

    @Override
    public Header getHeader(String name) {
        for(Header header: headers) {

            if (header.getName().equals(name)) {
                return header;
            }
        }

        return null;
    }

    @Override
    public void setBody(Body body) {
        this.body = body;
    }

    @Override
    public Body getBody() {
        return body;
    }

    @Override
    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public Boolean isException() {
        return exception != null;
    }

    @Override
    public String getRawHeaders() {
        String raw = "";

        for (Header header: headers) {
            raw += header.getName() + ": " + header.getValue() + "\n";
        }

        return raw;
    }
}
