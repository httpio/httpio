package com.httpio.http.impl;

import com.httpio.http.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
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

        // Process URL connection.
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

        // con.setConnectTimeout(5000);
        // con.setReadTimeout(5000);
        // con.setInstanceFollowRedirects(false);

        // HttpUrlConnection.setFollowRedirects(false);
        // if (status == HttpURLConnection.HTTP_MOVED_TEMP
        //  || status == HttpURLConnection.HTTP_MOVED_PERM) {
        //    String location = con.getHeaderField("Location");
        //    URL newUrl = new URL(location);
        //    con = (HttpURLConnection) newUrl.openConnection();
        //}

        // con.setDoOutput(true);
        // DataOutputStream out = new DataOutputStream(con.getOutputStream());
        // out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
        // out.flush();
        // out.close();

        for(Header header: request.getHeaders()) {
            connection.setRequestProperty(header.getName(), header.getValue());
        }

        if (request.getBody() != null) {
            String content = request.getBody().getContent();

            if (content != null) {
                connection.setDoOutput(true);

                try {
                    connection.getOutputStream().flush();
                    connection.getOutputStream().write(content.getBytes());
                    connection.getOutputStream().close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
