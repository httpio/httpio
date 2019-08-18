package com.httpio.app.models.requests;

import com.httpio.app.models.Request;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class RequestTest {

    @Test
    public void getResource() {
        Request r1 = new Request(UUID.randomUUID().toString());

        r1.setResource("/offers");

        Request r2 = new Request(UUID.randomUUID().toString());

        r2.setParent(r1);

        r2.setResource("/10");

        assertEquals("/offers/10", r2.getResourceFull());
    }
}