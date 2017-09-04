package com.dabakovich.entity;

import com.dabakovich.handler.State;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
public class User {

    private String id;
    @JsonProperty("telegram-id")
    private int telegramId;
    @JsonProperty("username")
    private String userName;
    private Schedule schedule;
    private State state = State.START_STATE;
    private String languageTag = "uk";

    public void moveStartDateOneDayAhead() {
        this.schedule.setStartDate(this.schedule.getStartDate().plusDays(1));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(int telegramId) {
        this.telegramId = telegramId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
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
                ", telegramId=" + telegramId +
                ", userName='" + userName + '\'' +
                ", schedule=" + schedule +
                ", state=" + state +
                ", languageTag='" + languageTag + '\'' +
                '}';
    }
}
