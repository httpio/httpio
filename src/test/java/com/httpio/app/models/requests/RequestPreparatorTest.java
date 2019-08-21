package com.httpio.app.models.requests;

import com.httpio.app.models.Request;
import com.httpio.app.services.Http;
import com.httpio.app.services.Http.Methods;
import com.httpio.app.models.Profile;
import com.httpio.app.modules.Item;
import com.httpio.app.services.Http.Protocols;
import com.httpio.app.services.HTTPRequestPreparator;
import com.httpio.app.services.HTTPRequestPreparator.RequestPrepared;
import org.junit.Test;

import java.net.MalformedURLException;

import static junit.framework.TestCase.assertEquals;

public class RequestPreparatorTest {
    @Test
    public void toRaw() throws MalformedURLException {
        Http http = new Http();

        Profile profile = new Profile("p") {{
            setBaseURL("http://ofiko.pl/api");

            addHeader(new Item("r1h1", "Accept", "application/json, text/javascript, */*; q=0.01"));
            addHeader(new Item("r1h2", "Accept-Encoding", "gzip, deflate"));
            addHeader(new Item("r1h3", "Accept-Language", "en-US,en;q=0.9,pl;q=0.8"));
            addHeader(new Item("r1h4", "Connection", "keep-alive"));
            addHeader(new Item("r1h7", "Cookie", "PHPSESSID={SESSION_ID}"));
            addHeader(new Item("r1h11", "User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36"));
        }};

        profile.addVariable("SESSION_ID", "fmm7thejkd06o4dkkj7n0m7onq");

        // Prepare request
        Request request = new Request(){{
            setMethod(http.getMethodById(Methods.GET));
            setUrl("/offers/10");

            addHeader(new Item("r1h5", "Content-Length", "92"));
            addHeader(new Item("r1h6", "Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));
            addHeader(new Item("r1h9", "Origin", "http://core.pbobryk.mx360.info"));
            addHeader(new Item("r1h10", "Referer", "http://core.pbobryk.mx360.info/site/MatrixNewUI/module/login/type/lockScreen"));
            addHeader(new Item("r1h12", "X-Requested-With", "XMLHttpRequest"));

            setBody("username=foo");

            addParameter(new Item("p1", "userId", "10"));
            addParameter(new Item("p2", "tree", "1"));
        }};

        // Preparator
        HTTPRequestPreparator httpRequestPreparator = new HTTPRequestPreparator();

        RequestPrepared prepared = httpRequestPreparator.prepare(request, profile);

        // Formatter formatter = new Formatter(profile, request);
        String raw = prepared.toRaw();

        assertEquals("GET /api/offers/10?tree=1&userId=10 HTTP/1.1\n" +
                "Cookie: PHPSESSID=fmm7thejkd06o4dkkj7n0m7onq\n" +
                "Origin: http://core.pbobryk.mx360.info\n" +
                "Accept: application/json, text/javascript, */*; q=0.01\n" +
                "X-Requested-With: XMLHttpRequest\n" +
                "Connection: keep-alive\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36\n" +
                "Referer: http://core.pbobryk.mx360.info/site/MatrixNewUI/module/login/type/lockScreen\n" +
                "Host: ofiko.pl\n" +
                "Accept-Encoding: gzip, deflate\n" +
                "Accept-Language: en-US,en;q=0.9,pl;q=0.8\n" +
                "Content-Length: 12\n" +
                "Content-Type: application/x-www-form-urlencoded; charset=UTF-8\n" +
                "\n" +
                "username=foo", raw);
    }
}