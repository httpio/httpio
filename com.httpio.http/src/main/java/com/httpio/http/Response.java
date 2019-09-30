package com.httpio.http;

import java.util.List;

public interface Response {
    void setCode(Code code);

    Code getCode();

    void setHeaders(List<Header> headers);

    List<Header> getHeaders();

    Header getHeader(String name);

    void setBody(Body body);

    Body getBody();

    void setException(Exception exception);

    Exception getException();

    Boolean isException();

    String getRawHeaders();
}