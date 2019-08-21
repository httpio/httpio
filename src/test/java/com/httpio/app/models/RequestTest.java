package com.httpio.app.models;

import org.junit.Test;

import static org.junit.Assert.*;

public class RequestTest {
    @Test
    public void getURLFull() {

        Request a = new Request(){{
            setUrl("/offers");
        }};

        Request b = new Request(){{
            setUrl("/10");
        }};

        Request c = new Request(){{
            setUrl("/comments");
        }};

        a.addRequest(b);
        b.addRequest(c);

        assertEquals("/offers", a.getURLFull());
        assertEquals("/offers/10", b.getURLFull());
        assertEquals("/offers/10/comments", c.getURLFull());
    }

    @Test
    public void getURLFullStandalone() {
        Request a = new Request(){{
            setUrl("/offers");
            setStandalone(true);
        }};

        Request b = new Request(){{
            setUrl("/10");
            setStandalone(true);
        }};

        Request c = new Request(){{
            setUrl("/comments");
            setStandalone(true);
        }};

        a.addRequest(b);
        b.addRequest(c);

        assertEquals("/offers", a.getURLFull());
        assertEquals("/10", b.getURLFull());
        assertEquals("/comments", c.getURLFull());
    }
}