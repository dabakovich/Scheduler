package com.dabakovich.entity.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dabak on 18.08.2017, 15:00.
 */
public class MessageToSend {

    private int chat_id;
    private String text;
    @JsonProperty("parse_mode")
    private String parseMode;
    @JsonProperty("disable_web_page_preview")
    private boolean disableWebPagePreview = false;

    public MessageToSend() {
    }

    public MessageToSend(int chat_id, String text) {
        this.chat_id = chat_id;
        this.text = text;
    }

    public int getChat_id() {
        return chat_id;
    }

    public void setChat_id(int chat_id) {
        this.chat_id = chat_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getParseMode() {
        return parseMode;
    }

    public void setParseMode(String parseMode) {
        this.parseMode = parseMode;
    }

    public boolean isDisableWebPagePreview() {
        return disableWebPagePreview;
    }

    public void setDisableWebPagePreview(boolean disableWebPagePreview) {
        this.disableWebPagePreview = disableWebPagePreview;
    }

    @Override
    public String toString() {
        return "MessageToSend{" +
                "chat_id=" + chat_id +
                ", text='" + text + '\'' +
                ", parseMode='" + parseMode + '\'' +
                ", disableWebPagePreview=" + disableWebPagePreview +
                '}';
    }
}
