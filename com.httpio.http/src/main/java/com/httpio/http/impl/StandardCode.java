package com.httpio.http.impl;

import com.httpio.http.Code;

// https://httpstatuses.com
public class StandardCode implements Code {
    private int code;

    public StandardCode(int code) {
        this.code = code;
    }

    @Override
    public int getValueInt() {
        return code;
    }

    @Override
    public Boolean isInformational() {
        return code >= 100 && code <= 199;
    }

    @Override
    public Boolean isSuccess() {
        return code >= 200 && code <= 299;
    }

    @Override
    public Boolean isRedirection() {
        return code >= 300 && code <= 399;
    }

    @Override
    public Boolean isClientError() {
        return code >= 400 && code <= 499;
    }

    @Override
    public Boolean isServerError() {
        return code >= 500 && code <= 599;
    }
}
