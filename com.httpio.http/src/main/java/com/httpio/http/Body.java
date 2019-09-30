package com.httpio.http;

import com.httpio.http.impl.StandardBody;

public interface Body {
    void setContent(String content);
    String getContent();

    static Body create(String content) {
        return new StandardBody(content);
    }
}
