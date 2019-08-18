package com.httpio.app.services;

import com.httpio.app.services.Http.Methods;
import com.httpio.app.models.Profile;
import com.httpio.app.models.Request;
import com.httpio.app.services.HTTPSender.Response;
import com.httpio.app.services.Http.Protocols;
import org.junit.Test;

public class HTTPSenderTest {

    @Test
    public void send() throws Exception {
        Logger logger = new Logger();

        HTTPSender httpSender = new HTTPSender();
        httpSender.setLogger(logger);

        Http http = new Http();

        httpSender.setRequestPreparator(new RequestPreparator());

        Profile profile = new Profile("p") {{
            setHost("jsonplaceholder.typicode.com");
            setProtocol(http.getProtocolById(Protocols.HTTP));
        }};

        Request request = new Request();

        request.setMethod(http.getMethodById(Methods.GET));
        request.setResource("/todos/1");

        Response response = httpSender.send(request, profile);
    }
}