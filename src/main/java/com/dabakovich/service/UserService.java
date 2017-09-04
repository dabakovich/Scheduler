package com.dabakovich.service;

import com.dabakovich.entity.DayPlane;
import com.dabakovich.entity.User;
import com.dabakovich.handler.State;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import java.time.LocalDate;

/**
 * Created by dabak on 14.08.2017, 17:59.
 */
public interface UserService {

    User getOne(org.telegram.telegrambots.api.objects.User telegramUser);

    User getByTelegramId(Integer telegramId);

    State getState(Integer telegramId);

    User getByTelegramIdOrSave(org.telegram.telegrambots.api.objects.User user);

    boolean togglePauseForTelegramUser(Integer telegramId);

    void save(User user);

    void setState(Integer telegramId, State state);

    SendMessage dayPlaneForDateMassage(User user, LocalDate date);

    void stopScheduleByTelegramId(Integer telegramId);

    void setLanguageByTelegramId(Integer telegramId, String messageText);

    User getOneOrSave(User user);

    void setLocaleForUser(String id, String locale);

    SendMessage dayPlaneForDateMassage(User user, DayPlane dayPlane, LocalDate date);
}
