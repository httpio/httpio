package com.httpio.app.services;

import com.google.inject.Inject;
import com.httpio.app.models.Profile;
import com.httpio.app.models.Request;
import com.httpio.app.services.HTTPRequestPreparator.RequestPrepared;
import javafx.scene.control.skin.TableHeaderRow;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HTTPSender {
    private HTTPRequestPreparator httpRequestPreparator;

    private Logger logger;

    @Inject
    public void setHttpRequestPreparator(HTTPRequestPreparator httpRequestPreparator) {
        this.httpRequestPreparator = httpRequestPreparator;
    }

    @Inject
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * Execute request with given profile and returns result.
     *
     * @param request
     * @param profile
     *
     * @return
     */
    public Response send(Request request, Profile profile) throws Exception {
        RequestPrepared prepared = httpRequestPreparator.prepare(request, profile);

        URL url = new URL(prepared.getUrl());

        logger.log("Start preparing request " + request.getId() + ".");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(prepared.getMethod());

        for(Map.Entry<String, String> entry: prepared.getHeaders().entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }

        // Process response
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));

        logger.log("Processing response of request request " + request.getId() + ".");

        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }

        // Get headers
        HashMap<String, ArrayList<String>> headers = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }

            if (headers.containsKey(entry.getKey()) == false) {
                headers.put(entry.getKey(), new ArrayList<>());
            }

            headers.get(entry.getKey()).addAll(entry.getValue());
        }

        Response response = new Response(connection.getResponseCode(), headers, stringBuilder.toString());

        return response;
    }

    /**
     * Represents response of request.
     */
    public static class Response {
        int code;
        HashMap<String, ArrayList<String>> headers = new HashMap<>();
        String body;

        public Response(int code, HashMap<String, ArrayList<String>> headers, String body) {
            this.code = code;
            this.body = body;

            // Put headers.
            this.headers.putAll(headers);
        }

        public int getCode() {
            return code;
        }

        public HashMap<String, ArrayList<String>> getHeaders() {
            return headers;
        }

        public String getHeadersRaw() {
            String raw = "";

            for (Map.Entry<String, ArrayList<String>> entry : headers.entrySet()) {

                for(String value: entry.getValue()) {
                    raw += entry.getKey() + ": " + value + "\n";
                }
            }

            return raw;

        }

        public String getBody() {
            return body;
        }
    }
}
