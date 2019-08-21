package com.httpio.app.services;

import com.httpio.app.models.Profile;
import com.httpio.app.models.Request;
import com.httpio.app.modules.Item;
import com.httpio.app.services.Http.Protocol;
import com.httpio.app.services.Http.Protocols;
import org.apache.commons.text.StringSubstitutor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Prepares the request for general processing.
 */
public class HTTPRequestPreparator {
    public RequestPrepared prepare(Request request, Profile profile) throws MalformedURLException {
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
        String url = profile.getBaseURL();

        // If url is given, then I change url. Otherwise I set url to "/" according with RFC2616.
        if (request.getURLFull() != null) {
            url += request.getURLFull();
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
            url += "?";

            for(Map.Entry<String, String> parameter: parameters.entrySet()) {
                url += parameter.getKey() + "=" + parameter.getValue() + "&";
            }

            url = url.substring(0, url.length() - 1);
        }

        // URL netURL = new URL(url);

        prepared.setUrl(substitutor.replace(url));

        // Protocol
        // prepared.setProtocol(profile.getProtocol());

        // Headers
        HashMap<String, String> headers = new HashMap<>();

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

        // Replace headers
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
        String url;
        Protocol protocol;

        HashMap<String, String> headers = new HashMap<>();

        String body;

        /**
         * Method
         */
        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        /**
         * URL
         */
        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        /**
         * Headers
         */
        public HashMap<String, String> getHeaders() {
            return headers;
        }

        public void setHeaders(HashMap<String, String> headers) {
            this.headers.clear();
            this.headers.putAll(headers);
        }

        /**
         * Body
         */
        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String toRaw() throws MalformedURLException {
            // Lines
            ArrayList<String> lines = new ArrayList<>();

            // Copy headers
            HashMap<String, String> headers = new HashMap<>(this.headers);

            // First line
            String lineA = "";
            if (method != null) {
                lineA += method + " ";
            }

            URL urlNet = new URL(url);

            headers.put("Host", urlNet.getHost());

            if (urlNet.getPath() != null && urlNet.getQuery() != null) {
                lineA += urlNet.getPath() + "?" + urlNet.getQuery();
            } else if (urlNet.getPath() != null) {
                lineA += urlNet.getPath();
            } else if (urlNet.getQuery() != null) {
                lineA += "/?" + urlNet.getQuery();
            } else {
                // This should not happened.
                lineA += "/";
            }

            lineA += " HTTP/1.1";

            lines.add(lineA);

            // headers
            for(Map.Entry<String, String> entry: headers.entrySet()) {
                lines.add(entry.getKey() + ": " + entry.getValue());
            }

            if (body != null) {
                lines.add("\n" + body);
            }

            // Create RAW
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
