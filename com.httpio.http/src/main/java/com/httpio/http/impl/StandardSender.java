package com.httpio.http.impl;

import com.httpio.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StandardSender implements Sender {
    @Override
    public Response send(Request request) {
        URL url;
        Response response = new StandardResponse();

        try {
            url = new URL(request.getUrl());
        } catch (MalformedURLException e) {
            response.setException(e);

            return response;
        }

        HttpURLConnection connection;

        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            response.setException(e);

            return response;
        }

        try {
            connection.setRequestMethod(request.getMethod().getHTTPValue());
        } catch (ProtocolException e) {
            response.setException(e);

            return response;
        }

        for(Header header: request.getHeaders()) {
            connection.setRequestProperty(header.getName(), header.getValue());
        }

        // Process response
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        BufferedReader bufferedReader = null;


        // Read response code
        Code code;

        try {
            code = new StandardCode(connection.getResponseCode());

            response.setCode(code);
        } catch (IOException e) {
            response.setException(e);
        }

        boolean isError = false;

        // Read response content
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
        } catch (IOException e) {
            response.setException(e);

            isError = true;
        }

        // There is error. So I read error stream.
        if (isError) {
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), Charset.forName("UTF-8")));

            try {
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (IOException e) {
                response.setException(e);
            }
        } else {
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (IOException e) {
                response.setException(e);
            }
        }

        // Get headers
        // HashMap<String, ArrayList<String>> headers = new HashMap<>();
        ArrayList<Header> headers = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }

            for(String value: entry.getValue()) {
                headers.add(new StandardHeader(entry.getKey(), value));
            }
        }

        response.setBody(new StandardBody(stringBuilder.toString()));
        response.setHeaders(headers);

        return response;
    }
}
