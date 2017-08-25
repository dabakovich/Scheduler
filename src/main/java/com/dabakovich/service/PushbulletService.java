package com.dabakovich.service;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dabak on 13.08.2017, 18:25.
 */
@Service
public class PushbulletService {

    private Logger logger = LoggerFactory.getLogger(PushbulletService.class);
    private static final Header TOKEN_HEADER = new BasicHeader("Access-Token", "o.tifOXZMGfJk7WKNiICX23W5Ri18G7Di5");
    private static final String URL_PUSHES = "https://api.pushbullet.com/v2/pushes";
    private final HttpsService httpsService;

    @Autowired
    public PushbulletService(HttpsService httpsService) {
        this.httpsService = httpsService;
    }

    public void pushNote(String title, String body, String email) {
        List<BasicNameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("type", "note"));
        parameters.add(new BasicNameValuePair("title", title));
        parameters.add(new BasicNameValuePair("body", body));
        parameters.add(new BasicNameValuePair("email", email));

        System.out.println("SENT request to " + URL_PUSHES + " with parameters " + parameters);
//        logger.info("SENT request to " + URL_PUSHES + " with parameters " + parameters);
        httpsService.sendPost(URL_PUSHES, new Header[]{TOKEN_HEADER}, parameters);
    }

    public void pushLink(String title, String body, String link, String email) {
        List<BasicNameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("type", "link"));
        parameters.add(new BasicNameValuePair("title", title));
        parameters.add(new BasicNameValuePair("body", body));
        parameters.add(new BasicNameValuePair("url", link));
        parameters.add(new BasicNameValuePair("email", email));

//        System.out.println("SENT request to " + URL_PUSHES + " with parameters " + parameters);
//        logger.info("SENT request to " + URL_PUSHES + " with parameters " + parameters);
        httpsService.sendPost(URL_PUSHES, new Header[]{TOKEN_HEADER}, parameters);
    }
}
