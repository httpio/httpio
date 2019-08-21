package com.httpio.app.services;

import com.httpio.app.models.Request;
import org.junit.Test;
import static org.junit.Assert.*;

public class RequestsCreatorTest {
    @Test
    public void createFromHTTPRaw() {

        RequestsCreator requestsCreator = new RequestsCreator();

        requestsCreator.setHttp(new Http());

        String raw =
            "POST /cgi-bin/process.cgi HTTP/1.1\n" +
            "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\n" +
            "Host: www.tutorialspoint.com\n" +
            "Content-Type: application/x-www-form-urlencoded\n" +
            "Content-Length: length\n" +
            "Accept-Language: en-us\n" +
            "Accept-Encoding: gzip, deflate\n" +
            "Connection: Keep-Alive\n" +
            "\n" +
            "licenseID=string&content=string&/paramsXML=string"
        ;

        Request request = requestsCreator.createFromHTTPRaw(raw);

        System.out.println(request.getId());
    }

    @Test
    public void createFromHTTPRawWithoutMethod() {

        RequestsCreator requestsCreator = new RequestsCreator();

        requestsCreator.setHttp(new Http());

        String raw =
            "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\n" +
            "Host: www.tutorialspoint.com\n" +
            "Content-Type: application/x-www-form-urlencoded\n" +
            "Content-Length: length\n" +
            "Accept-Language: en-us\n" +
            "Accept-Encoding: gzip, deflate\n" +
            "Connection: Keep-Alive\n" +
            "\n" +
            "licenseID=string&content=string&/paramsXML=string"
        ;

        Request request = requestsCreator.createFromHTTPRaw(raw);

        System.out.println(request.getId());
    }
}