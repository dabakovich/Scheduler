package com.dabakovich.entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
public class User {

    private String id;
    private int chatId;
    private String name;
    private String email;
    private String languageTag = "uk";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLanguageTag() {
        return languageTag;
    }

    public void setLanguageTag(String languageTag) {
        this.languageTag = languageTag;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", chatId=" + chatId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", languageTag='" + languageTag + '\'' +
                '}';
    }
}
