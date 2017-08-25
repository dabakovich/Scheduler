package com.dabakovich;

import com.dabakovich.entity.telegram.MessageToSend;
import com.dabakovich.service.HttpsService;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by dabak on 21.08.2017, 14:46.
 */

public class Application {

    public static void main(String[] args) {
        HttpsService httpsService = new HttpsService();
        MessageToSend message = new MessageToSend();
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:messages", "classpath:books");
        messageSource.setDefaultEncoding("UTF-8");

        String chatURL = "https://api.telegram.org/bot357114043:AAE8hacFVBi_n0rmFIY-Tt_lbf7Xw4vV2SE/sendMessage";
        String url = "https://wol.jw.org/uk/wol/l/r15/lp-k?q=", uri;
        String urn = "Єр 29; 2Цр 24:18-20; 2Хр 36:11-14; Єр 49:34-39; 50";
        try {
            uri = url + URLEncoder.encode(urn, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            uri = url + urn;
        }
        String text = messageSource.getMessage("telegram.text", null, Locale.forLanguageTag("uk"));
        text += messageSource.getMessage("book.ge", null, Locale.forLanguageTag("uk"));
        text += "[link](" + uri + ")";
        message.setChat_id(240509541);
        message.setText(text);
        message.setDisableWebPagePreview(true);
        message.setParseMode("Markdown");
        List<BasicNameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("chat_id", Integer.toString(240509541)));
        parameters.add(new BasicNameValuePair("text", text));
        parameters.add(new BasicNameValuePair("parse_mode", "Markdown"));
        parameters.add(new BasicNameValuePair("disable_web_page_preview", "true"));
        httpsService.sendPost(chatURL, message);
    }
}
