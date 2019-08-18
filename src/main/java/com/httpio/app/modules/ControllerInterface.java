package com.httpio.app.modules;

public interface ControllerInterface {
    void prepare();

    default void refresh() {}
}
