package com.httpio.app.services;

import com.httpio.app.models.Profile;
import com.httpio.app.models.Request;
import com.httpio.app.modules.Item;
import com.httpio.app.services.Http.Protocol;
import com.httpio.app.services.Http.Protocols;
import org.apache.commons.text.StringSubstitutor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Prepares the application for general processing.
 */
public class RequestPreparator {
    public RequestPrepared prepare(Request request, Profile profile) {
        RequestPrepared prepared = new RequestPrepared();

        // Variables
        Map<String, String> variables = new HashMap<>();

        for(Item variable: profile.getVariables()) {
            variables.put(variable.getName(), variable.getValue());
        }

        StringSubstitutor substitutor = new StringSubstitutor(variables, "{", "}");

        String lineA = "";

        if (request.getMethod() != null) {
            prepared.setMethod(request.getMethod().toHTTPHeader());
        }

        // Resource
        String resource = "";

        if (request.getResourceFull() != null) {
            resource += request.getResourceFull();
        }

        // Parameters
        HashMap<String, String> parameters = new HashMap<>();

        for(Item item: profile.getParameters()) {
            parameters.put(item.getName(), item.getValue());
        }

        for(Item item: request.getParameters()) {
            parameters.put(item.getName(), item.getValue());
        }

        if (parameters.size() > 0) {
            resource += "?";

            for(Map.Entry<String, String> parameter: parameters.entrySet()) {
                resource += parameter.getKey() + "=" + parameter.getValue() + "&";
            }

            resource = resource.substring(0, resource.length() - 1);
        }

        prepared.setResource(substitutor.replace(resource));

        // Protocol
        prepared.setProtocol(profile.getProtocol());

        // Headers
        HashMap<String, String> headers = new HashMap<>();

        if (profile.getHost() != null) {
            headers.put("Host", profile.getHost());
        }

        for(Item header: profile.getHeaders()) {
            headers.put(header.getName(), header.getValue());
        }

        for(Item header: request.getHeaders()) {
            headers.put(header.getName(), header.getValue());
        }

        if (request.getBody() != null) {
            headers.put("Content-Length", Integer.toString(request.getBody().getBytes().length));

            prepared.setBody(substitutor.replace(request.getBody()));
        }

        // Replace headeres
        HashMap<String, String> headersReplaced = new HashMap<>();

        for(Map.Entry<String, String> entry: headers.entrySet()) {
            headersReplaced.put(entry.getKey(), substitutor.replace(entry.getValue()));
        }

        prepared.setHeaders(headersReplaced);


        return prepared;
    }

    /**
     * Represents prepared request.
     */
    public static class RequestPrepared {
        String method;
        String resource;
        Protocol protocol;

        HashMap<String, String> headers = new HashMap<>();

        String body;

        public String getUrl() {
            return protocol + "://" + headers.get("Host") + resource;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getResource() {
            return resource;
        }

        public void setResource(String resource) {
            this.resource = resource;
        }

        public Protocol getProtocol() {
            return protocol;
        }

        public void setProtocol(Protocol protocol) {
            this.protocol = protocol;
        }

        public HashMap<String, String> getHeaders() {
            return headers;
        }

        public void setHeaders(HashMap<String, String> headers) {
            this.headers.clear();
            this.headers.putAll(headers);
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String toRaw() {
            String lineA = "";
            ArrayList<String> lines = new ArrayList<>();

            if (method != null) {
                lineA += method + " ";
            }

            if (resource != null) {
                lineA += resource;
            }

            // Protocol
            if (protocol != null) {
                if (protocol.getId() == Protocols.HTTP || protocol.getId() == Protocols.HTTPS) {
                    lineA += " HTTP/1.1";
                }
            }

            lines.add(lineA);

            for(Map.Entry<String, String> entry: headers.entrySet()) {
                lines.add(entry.getKey() + ": " + entry.getValue());
            }

            if (body != null) {
                lines.add("\n" + body);
            }

            String raw = "";
            int index = 0;
            int size = lines.size();

            for(String line: lines) {
                index++;

                if (index == size) {
                    raw += line;
                } else {
                    raw += line + "\n";
                }
            }

            return raw;
        }
    }
}
