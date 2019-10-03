package com.httpio.http.impl;

import org.junit.Test;

import static org.junit.Assert.*;

public class StandardCodeTest {

    @Test
    public void isInformational() {
        assertEquals(false, (new StandardCode(200)).isInformational());
        assertEquals(true, (new StandardCode(100)).isInformational());
        assertEquals(true, (new StandardCode(101)).isInformational());
    }

    @Test
    public void isSuccess() {
        assertEquals(true, (new StandardCode(200)).isSuccess());
        assertEquals(false, (new StandardCode(102)).isSuccess());
    }

    @Test
    public void isRedirection() {
        assertEquals(true, (new StandardCode(300)).isRedirection());
        assertEquals(false, (new StandardCode(100)).isRedirection());
    }

    @Test
    public void isClientError() {
        assertEquals(true, (new StandardCode(400)).isClientError());
        assertEquals(false, (new StandardCode(300)).isClientError());
    }

    @Test
    public void isServerError() {
        assertEquals(true, (new StandardCode(500)).isServerError());
        assertEquals(false, (new StandardCode(400)).isServerError());
    }
}