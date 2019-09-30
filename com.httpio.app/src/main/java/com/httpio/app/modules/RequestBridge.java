package com.httpio.app.modules;

import com.httpio.app.models.Profile;
import com.httpio.app.models.Request;
import com.httpio.http.Body;
import com.httpio.http.Header;
import com.httpio.http.Method;
import com.httpio.http.impl.StandardBody;
import com.httpio.http.impl.StandardHeader;
import org.apache.commons.text.StringSubstitutor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestBridge implements com.httpio.http.Request {
    private Request request;
    private Profile profile;

    public RequestBridge(Request request, Profile profile) {
        this.request = request;
        this.profile = profile;
    }

    @Override
    public Method getMethod() {
        return request.getMethod();
    }

    @Override
    public List<Header> getHeaders() {
        // Variables
        StringSubstitutor substitutor = getSubstitutor();

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
        }

        // Replace headers
        ArrayList<Header> prepared = new ArrayList<>();

        for(Map.Entry<String, String> entry: headers.entrySet()) {
            prepared.add(new StandardHeader(entry.getKey(), substitutor.replace(entry.getValue())));
        }

        return prepared;
    }

    @Override
    public String getUrl() {
        StringSubstitutor substitutor = getSubstitutor();

        // Resource
        StringBuilder url = new StringBuilder(profile.getBaseURL());

        // If url is given, then I change url. Otherwise I set url to "/" according with RFC2616.
        if (request.getURLFull() != null) {
            url.append(request.getURLFull());
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
            url.append("?");

            for(Map.Entry<String, String> parameter: parameters.entrySet()) {
                url.append(parameter.getKey()).append("=").append(parameter.getValue()).append("&");
            }

            url = new StringBuilder(url.substring(0, url.length() - 1));
        }

        return substitutor.replace(url.toString());
    }

    @Override
    public Body getBody() {
        StringSubstitutor substitutor = getSubstitutor();

        Body body = new StandardBody();

        if (request.getBody() != null) {
            body.setContent(substitutor.replace(request.getBody()));
        }

        return body;
    }

    private StringSubstitutor getSubstitutor() {
        Map<String, String> variables = new HashMap<>();

        for(Item variable: profile.getVariables()) {
            variables.put(variable.getName(), variable.getValue());
        }

        return new StringSubstitutor(variables, "{", "}");
    }

    public String getRAW() throws MalformedURLException {
        // Lines
        ArrayList<String> lines = new ArrayList<>();

        // First line
        String lineA = "";

        if (getMethod() != null) {
            lineA += getMethod().getHTTPValue() + " ";
        }

        URL urlNet = null;

        urlNet = new URL(getUrl());

        List<Header> headers = getHeaders();

        headers.add(new StandardHeader("Host", urlNet.getHost()));

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
        for(Header header: headers) {
            lines.add(header.getName() + ": " + header.getValue());
        }

        if (getBody() != null) {
            if (getBody().getContent() != null) {
                lines.add("\n" + getBody().getContent());
            }
        }

        // Create RAW
        StringBuilder raw = new StringBuilder();

        int index = 0;
        int size = lines.size();

        for(String line: lines) {
            index++;

            if (index == size) {
                raw.append(line);
            } else {
                raw.append(line).append("\n");
            }
        }

        return raw.toString();
    }
}