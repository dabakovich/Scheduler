package com.dabakovich.service;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiConstants;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updates.SetWebhook;

import java.io.File;
import java.io.IOException;

/**
 * Created by dabak on 17.08.2017, 14:23.
 */
public class TelegramService {

    private static final Header CONTENT_TYPE = new BasicHeader("Content-Type", "application/x-www-form-urlencoded");
    private Logger logger = LoggerFactory.getLogger(TelegramService.class);

    private final String serverURL;
    private final String telegramURL;
    private final String certPath;
    private final HttpsService httpsService;

    public TelegramService(String serverURL, String token, String certPath, HttpsService httpsService) {
        this.serverURL = serverURL;
        this.certPath = certPath;
        this.telegramURL = ApiConstants.BASE_URL + token;
        this.httpsService = httpsService;

        setWebhook();
    }

    private void setWebhook() {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        builder.addTextBody(SetWebhook.URL_FIELD, serverURL);
        if (certPath != null) {
            File certificate = new File(certPath);
            builder.addBinaryBody(SetWebhook.CERTIFICATE_FIELD, certificate, ContentType.TEXT_PLAIN, certificate.getName());
        }
        HttpEntity httpEntity = builder.build();

        try {
            httpsService.sendPost(telegramURL + "/" + SetWebhook.PATH, null, httpEntity);
            logger.info("Webhook configured for bot " + serverURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(SendMessage dayPlaneMessage) {
        if (dayPlaneMessage == null) return;
        try {
            httpsService.sendPost(telegramURL + "/" + SendMessage.PATH, dayPlaneMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
