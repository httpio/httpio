package com.httpio.app.util;

public class Functions {
    public static <T> T ifnull(T variable, T def) {
        if (variable == null) {
            return def;
        } else {
            return variable;
        }
    }
}
