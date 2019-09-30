package com.httpio.http.impl;

import com.httpio.http.Body;

public class StandardBody implements Body {
    private String content;

    public StandardBody() {}

    public StandardBody(String content) {
        this.content = content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getContent() {
        return this.content;
    }
}
