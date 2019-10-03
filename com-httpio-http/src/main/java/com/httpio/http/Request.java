package com.httpio.http;

import java.util.List;

public interface Request {
    Method getMethod();

    List<Header> getHeaders();

    String getUrl();

    Body getBody();
}