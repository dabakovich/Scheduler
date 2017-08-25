package com.dabakovich.service;

import com.dabakovich.entity.telegram.MessageToSend;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

/**
 * Created by dabak on 17.08.2017, 14:23.
 */
public class TelegramService {

    private static final Header CONTENT_TYPE = new BasicHeader("Content-Type", "application/x-www-form-urlencoded");

    private final String TOKEN;
    private final String URL;
    private final HttpsService httpsService;

    public TelegramService(String token, HttpsService httpsService) {
        this.TOKEN = token;
        this.URL = "https://api.telegram.org/bot" + TOKEN;

        this.httpsService = httpsService;
    }

    public void sendMessage(MessageToSend message) {
        String response = httpsService.sendPost(URL + "/sendMessage", message);
        System.out.println(response);
    }
}
