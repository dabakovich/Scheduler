package com.dabakovich.controller;

import com.dabakovich.handler.BibleSchedulerBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.objects.Update;

/**
 * Created by dabak on 17.08.2017, 14:51.
 */
@RestController
@RequestMapping("/telegram")
public class TelegramController {

    private final BibleSchedulerBot bibleSchedulerBot;

    @Autowired
    public TelegramController(BibleSchedulerBot bibleSchedulerBot) {
        this.bibleSchedulerBot = bibleSchedulerBot;
    }

    @PostMapping(consumes = {"application/json; charset=UTF-8", "*/*;charset=UTF-8"})
    public BotApiMethod handleUpdate(@RequestBody Update update) {
        return bibleSchedulerBot.handleUpdate(update);
    }

    @PostMapping("/send")
    public void sendMessage(@RequestParam int chat_id, @RequestParam String text) {

    }
}
