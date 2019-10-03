package com.httpio.http;

public interface Code {

    // https://www.restapitutorial.com/httpstatuscodes.html

    int getValueInt();

    Boolean isInformational();
    Boolean isSuccess();
    Boolean isRedirection();
    Boolean isClientError();
    Boolean isServerError();
}
