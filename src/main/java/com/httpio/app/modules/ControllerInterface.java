package com.httpio.app.modules;

public interface ControllerInterface {
    default void prepare() {};
    default void refresh() {}
}
