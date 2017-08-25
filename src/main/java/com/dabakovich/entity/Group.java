package com.dabakovich.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by dabak on 13.08.2017, 23:58.
 */
@Document(collection = "reading-group")
public class Group {

    @Id
    private String id;

    @DBRef
    private List<User> users;

    private boolean active;

//    @JsonSerialize(using = LocalDateSerializer.class)
//    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate startDate;

    private ScheduleType scheduleType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(ScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id='" + id + '\'' +
                ", users=" + users +
                ", startDate=" + startDate +
                ", scheduleType=" + scheduleType +
                '}';
    }
}
