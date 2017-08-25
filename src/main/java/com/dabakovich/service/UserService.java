package com.dabakovich.service;

import com.dabakovich.entity.DayPlane;
import com.dabakovich.entity.User;

import java.time.LocalDate;

/**
 * Created by dabak on 14.08.2017, 17:59.
 */
public interface UserService {

    User getByName(String name);

    void sendDayPlane(User user, DayPlane dayPlane, LocalDate date);

    User getOneOrSave(User user);

    void setLocaleForUser(String id, String locale);
}
