package com.httpio.app.models;

import com.httpio.app.services.Http;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.util.Properties;

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

    @Test
    public void duplicate() throws NoSuchAlgorithmException {
        Http http = new Http();

        Request a = new Request(){{
            setName("a");
            setBody("...");
            setMethod(http.getMethodById(Http.Methods.GET));
            setUrl("/offers");

            addHeader("Content-Type", "application/json");
            addParameter("userId", "10");
        }};

        Request b = new Request(){{
            setName("b");
            setBody("...");
            setMethod(http.getMethodById(Http.Methods.GET));
            setUrl("/10");

            addHeader("Content-Type", "application/json");
            addParameter("userId", "10");
        }};

        Request c = new Request(){{
            setName("c");
            setBody("...");
            setMethod(http.getMethodById(Http.Methods.GET));
            setUrl("/comments");

            addHeader("Content-Type", "application/json");
            addParameter("userId", "10");
        }};

        a.addRequest(b);
        b.addRequest(c);

        Request copy = a.duplicate();

        assertEquals(copy.getChecksum(new Properties(){{put("excludeId", "1");}}), a.getChecksum(new Properties(){{put("excludeId", "1");}}));
    }

    @Test
    public void getChecksum() throws NoSuchAlgorithmException {
        Http http = new Http();

        Request a = new Request(){{
            setName("a");
            setBody("...");
            setMethod(http.getMethodById(Http.Methods.GET));
            setUrl("/offers");

            addHeader("Content-Type", "application/json");
            addParameter("userId", "10");
        }};

        Request b = new Request(){{
            setName("b");
            setBody("...");
            setMethod(http.getMethodById(Http.Methods.GET));
            setUrl("/10");

            addHeader("Content-Type", "application/json");
            addParameter("userId", "10");
        }};

        Request c = new Request(){{
            setName("c");
            setBody("...");
            setMethod(http.getMethodById(Http.Methods.GET));
            setUrl("/comments");

            addHeader("Content-Type", "application/json");
            addParameter("userId", "10");
        }};

        a.addRequest(b);
        b.addRequest(c);

        assertEquals("087AA8685EE3D8552EF769ACDB37C9C6", a.getChecksum(new Properties(){{put("excludeId", "1");}}));
    }
}