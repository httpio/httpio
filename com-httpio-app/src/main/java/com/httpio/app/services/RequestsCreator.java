package com.httpio.app.services;

import com.google.inject.Inject;
import com.httpio.app.models.Request;
import com.httpio.http.Method;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class RequestsCreator {
    public Request createFromHTTPRaw(String raw) {
        RawDecoder decoder = new RawDecoder(raw);

        return decoder.decode();
    }

    private class RawDecoder {
        private String raw;

        public RawDecoder (String raw) {
            this.raw = raw;
        }

        public Request decode() {
            Request request = new Request();

            String[] lines = raw.split("\n");

            Boolean omitFirstLine = false;

            // Check if first line start from method. If yes, then I create method, url. If not then I create only
            // headers and body.
            String lineFirst = lines[0];

            String[] elements = lineFirst.split(" ");

            // StandardMethod
            String methodName = elements[0].trim();
            Method method = Method.create(methodName.toLowerCase());

            if (method != null) {
                request.setMethod(method);

                processResource(elements[1], request);

                // Set name like url
                request.setName("Imported from RAW '" + request.getUrl() + "'");

                omitFirstLine = true;
            } else {
                request.setName("Imported from RAW");
                request.setMethod(Method.create(Method.GET));
                request.setUrl("/...");
            }

            // First line
            for(String line: lines) {
                if (line.equals("")) {
                    break;
                }

                if (omitFirstLine) {
                    omitFirstLine = false;

                    continue;
                }

                String[] e = line.split(":");

                if (e.length == 2) {
                    String name = e[0].trim();
                    String value = e[1].trim();

                    request.addHeader(name, value);
                } else {
                    String name = e[0].trim();

                    request.addHeader(name);
                }

            }

            // StandardBody
            StringBuilder bodyBuilder = new StringBuilder();
            Boolean isBody = false;
            Boolean isBodyFirst = true;
            Boolean isBodyAppended = false;

            for(String line: lines) {
                if (isBody) {
                    if (isBodyFirst) {
                        bodyBuilder.append(line);

                        isBodyAppended = true;

                        isBodyFirst = false;
                    } else {
                        bodyBuilder.append("\n" + line);

                        isBodyAppended = true;
                    }

                    continue;
                } else {
                    if (line.equals("")) {
                        isBody = true;
                    }
                }
            }

            if (isBodyAppended) {
                request.setBody(bodyBuilder.toString());
            }

            return request;
        }

        private void processResource(String url, Request request) {
            try {
                url = URLDecoder.decode(url.trim(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            // Check if there are any params
            if (url.contains("?")) {
                // There are params
                String[] splited = url.split("\\?");

                request.setUrl(splited[0]);

                // Parameters
                String parameters = splited[1];

                for(String parameter: parameters.split("&")) {
                    parameter = parameter.trim();

                    String[] e = parameter.split("=");

                    if (e.length == 2) {
                        request.addParameter(e[0], e[1]);
                    } else {
                        request.addParameter(e[0]);
                    }
                }
            } else {
                request.setUrl(url);
            }
        }
    }
}
