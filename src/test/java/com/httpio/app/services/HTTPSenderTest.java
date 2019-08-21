package com.httpio.app.services;

import com.httpio.app.services.Http.Methods;
import com.httpio.app.models.Profile;
import com.httpio.app.models.Request;
import com.httpio.app.services.HTTPSender.Response;
import com.httpio.app.services.Http.Protocols;
import junit.framework.TestCase;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class HTTPSenderTest {

    @Test
    public void send() throws Exception {
        Logger logger = new Logger();

        HTTPSender httpSender = new HTTPSender();
        httpSender.setLogger(logger);

        Http http = new Http();

        httpSender.setHttpRequestPreparator(new HTTPRequestPreparator());

        Profile profile = new Profile("p") {{
            setBaseURL("http://jsonplaceholder.typicode.com");
        }};

        Request request = new Request();

        request.setMethod(http.getMethodById(Methods.GET));
        request.setUrl("/todos/1");

        Response response = httpSender.send(request, profile);

        assertEquals("{  \"userId\": 1,  \"id\": 1,  \"title\": \"delectus aut autem\",  \"completed\": false}", response.getBody());
    }
}