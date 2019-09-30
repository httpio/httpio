package com.httpio.http;

import com.httpio.http.impl.StandardMethod;

import java.util.ArrayList;
import java.util.List;

public interface Method {
    String GET = "get";
    String POST = "post";
    String PUT = "put";
    String DELETE = "delete";
    String HEAD = "head";
    String OPTIONS = "options";
    String PATCH = "patch";

    String getId();
    String getHTTPValue();

    static Method create(String method) {
        return new StandardMethod(method);
    }

    static List<Method> createAllMethodsList() {
        return new ArrayList<>() {{
            add(new StandardMethod(Method.GET));
            add(new StandardMethod(Method.POST));
            add(new StandardMethod(Method.PUT));
            add(new StandardMethod(Method.DELETE));
            add(new StandardMethod(Method.HEAD));
            add(new StandardMethod(Method.OPTIONS));
            add(new StandardMethod(Method.PATCH));
        }};
    }
}
