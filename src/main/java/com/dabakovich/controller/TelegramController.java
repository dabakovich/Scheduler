package com.dabakovich.controller;

import com.dabakovich.entity.DayPlane;
import com.dabakovich.entity.ScheduleType;
import com.dabakovich.entity.telegram.MessageToSend;
import com.dabakovich.entity.telegram.Update;
import com.dabakovich.repository.DayPlaneRepository;
import com.dabakovich.service.TelegramService;
import com.dabakovich.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Created by dabak on 17.08.2017, 14:51.
 */
@RestController
@RequestMapping("/telegram")
public class TelegramController {

    private final TelegramService telegramService;
    private final UserService userService;
    private final DayPlaneRepository dayPlaneRepository;

    @Autowired
    public TelegramController(TelegramService telegramService, UserService userService, DayPlaneRepository dayPlaneRepository) {
        this.telegramService = telegramService;
        this.userService = userService;
        this.dayPlaneRepository = dayPlaneRepository;
    }

    @PostMapping
    public void handleUpdate(@RequestBody Update update) {
        if (update.getMessage().getText().equals("send")) {
            int chatId = update.getMessage().getChat().getId();
            int daysBetween = (int) ChronoUnit.DAYS.between(LocalDate.of(2017,1,14), LocalDate.now());
            DayPlane dayPlane = dayPlaneRepository.findByScheduleTypeAndSequenceNumber(ScheduleType.CHRONOLOGICAL, daysBetween);
            userService.sendDayPlane(userService.getByName("David"), dayPlane, LocalDate.now());
        }
        System.out.println(update);
    }

    @PostMapping("/send")
    public void sendMessage(@RequestParam int chat_id, @RequestParam String text) {
        telegramService.sendMessage(new MessageToSend(chat_id, text));
    }
}
