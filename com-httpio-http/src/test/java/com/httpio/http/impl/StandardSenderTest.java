package com.httpio.http.impl;

import com.httpio.http.Header;
import com.httpio.http.Method;
import com.httpio.http.Response;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class StandardSenderTest {

    @Test
    public void send() {
        StandardSender sender = new StandardSender();

        StandardRequest request = new StandardRequest();

        request.setMethod(new StandardMethod(Method.GET));
        request.setUrl("https://jsonplaceholder.typicode.com/todos/1");

        Response response = sender.send(request);

        assertEquals("{  \"userId\": 1,  \"id\": 1,  \"title\": \"delectus aut autem\",  \"completed\": false}", response.getBody().getContent());
        assertEquals("true", response.getHeader("Access-Control-Allow-Credentials").getValue());
        assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type").getValue());
    }

    @Test
    public void sendWithException() {
        StandardSender sender = new StandardSender();

        StandardRequest request = new StandardRequest();

        request.setMethod(new StandardMethod(Method.GET));
        request.setUrl("htt://jsonplaceholder.typicode.com/todos/1");

        Response response = sender.send(request);

        assertEquals(true, response.isException());
        assertTrue(response.getException() instanceof Exception);
    }
}