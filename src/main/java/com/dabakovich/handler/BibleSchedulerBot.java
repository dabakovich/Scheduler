package com.dabakovich.handler;

import com.dabakovich.entity.Schedule;
import com.dabakovich.entity.ScheduleType;
import com.dabakovich.service.UserService;
import com.dabakovich.service.utils.LanguageContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Created by dabak on 31.08.2017, 18:57.
 */
@Component
public class BibleSchedulerBot {

    private UserService userService;
    private final LanguageContainer languageContainer;
    private final ReloadableResourceBundleMessageSource M;

    @Autowired
    public BibleSchedulerBot(LanguageContainer languageContainer, ReloadableResourceBundleMessageSource messages) {
//        this.userService = userService;
        this.languageContainer = languageContainer;
        this.M = messages;
    }

    public SendMessage handleUpdate(Update update) {
        SendMessage message = new SendMessage();
        if (update.hasMessage() && update.getMessage().hasText()) {
            State state = userService.getState(update.getMessage().getFrom().getId());
            switch (state) {
                case START_STATE:
                    message = messageOnStartCommand(update);
                    break;
                case MAIN_MENU:
                    message = messageOnMainMenu(update);
                    break;
                case NEW_SCHEDULER:
                case NEW_SCHEDULER_DATE:
                case NEW_SCHEDULER_MANUAL_DATE:
                case SEND_CURRENT_PASSAGES_CONFIRM:
                    message = messageOnNewSchedule(update, state);
                    break;
                case SETTINGS_MENU:
                    message = messageOnSettingsMenu(update);
                    break;
                case STOP_SCHEDULE:
                    message = messageOnStopSchedule(update);
                    break;
                case LANGUAGE:
                    message = messageOnLanguageMenu(update);
                    break;
                default: message = sendMessageDefault(update);
            }
        } else if (update.hasCallbackQuery()) {

        }
        return message;
    }

    private SendMessage messageOnMainMenu(Update update) {
        Locale locale = getLocale(update);
        String messageText = update.getMessage().getText();
        SendMessage newMessage;
        if (messageText.startsWith(M.getMessage("telegram.button.add", null, locale))) {
            newMessage = onNewScheduleChosen(update);
        } else if(messageText.startsWith(M.getMessage("telegram.button.settings", null, locale))) {
            newMessage = onSettingsChosen(update);
        } else newMessage = sendMessageDefault(update);

        return newMessage;
    }

    private SendMessage messageOnSettingsMenu(Update update) {
        Locale locale = getLocale(update);
        String messageText = update.getMessage().getText();
        SendMessage newMessage;
        if (messageText.startsWith(M.getMessage("telegram.button.pause", null, locale))) {
            newMessage = onPauseChosen(update);
        } else if(messageText.startsWith(M.getMessage("telegram.button.resume", null, locale))) {
            newMessage = onResumeChosen(update);
        } else if(messageText.startsWith(M.getMessage("telegram.button.stop", null, locale))) {
            newMessage = onStopChosen(update);
        } else if(messageText.startsWith(M.getMessage("telegram.button.language", null, locale))) {
            newMessage = onLanguageChosen(update);
        } else if(messageText.startsWith(M.getMessage("telegram.button.cancel", null, locale))) {
            newMessage = onSettingCancelChosen(update);
        } else newMessage = sendMessageDefault(update);

        return newMessage;
    }

    private SendMessage messageOnStopSchedule(Update update) {
        String messageText = update.getMessage().getText();
        SendMessage newMessage;
        if (messageText.startsWith(M.getMessage("telegram.button.yes", null, getLocale(update)))) {
            return onYesChosenForStopSchedule(update);
        }
        return onSettingsChosen(update);
    }

    @SuppressWarnings("unchecked")
    private SendMessage messageOnLanguageMenu(Update update) {
        List<String> languages = languageContainer.getLanguages();
        String messageText = update.getMessage().getText();
        String language = languages
                .stream()
                .filter(l -> l.substring(l.indexOf(":") + 1).equals(messageText))
                .findAny()
                .orElse(null);
        if (language != null) {
            userService.setLanguageByTelegramId(update.getMessage().getFrom().getId(), language);
            return sendMessageDefault(update);
        }
        return onSettingsChosen(update);
    }

    private SendMessage onNewScheduleChosen(Update update) {
        Message message = update.getMessage();
        com.dabakovich.entity.User user = userService.getOne(message.getFrom());
        Locale locale = getLocale(user);
        if (user.getSchedule() != null) {
            return new SendMessage()
                    .setChatId(message.getChatId())
                    .setText(M.getMessage("telegram.message.have_scheduler", null, locale));
        }
        ReplyKeyboardMarkup keyboard = getSchedulerTypesKeyboard(update);
        userService.setState(user.getTelegramId(), State.NEW_SCHEDULER);
        String[] scheduleTypes = M.getMessage("telegram.schedulers", null, locale).split(",");
        StringBuilder builder = new StringBuilder(M.getMessage("telegram.message.choose_scheduler", null, locale));
        for (int i = 0; i < scheduleTypes.length; i++) {
            builder.append((i + 1) + ". " + scheduleTypes[i].split(":")[0] + ".\n");
        }

        return new SendMessage()
                .setChatId(message.getChatId())
                .setText(builder.toString())
                .setReplyMarkup(keyboard);
    }

    private SendMessage onSettingsChosen(Update update) {
        com.dabakovich.entity.User user = userService.getByTelegramId(update.getMessage().getFrom().getId());
        user.setState(State.SETTINGS_MENU);
        userService.save(user);
        Locale locale = getLocale(user);
        ReplyKeyboardMarkup keyboard = getSettingsKeyboard(user);

        StringBuilder builder = new StringBuilder(M.getMessage("telegram.message.settings_menu", null, locale));
        if (user.getSchedule() != null) {
            if (user.getSchedule().isActive()) {
                builder.append(M.getMessage("telegram.description.pause", null, locale));
            } else builder.append(M.getMessage("telegram.description.resume", null, locale));
            builder.append(M.getMessage("telegram.description.stop", null, locale));
        }
        builder.append(M.getMessage("telegram.description.language", null, locale));
        builder.append(M.getMessage("telegram.description.cancel", null, locale));

        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(builder.toString())
                .setParseMode(ParseMode.MARKDOWN)
                .setReplyMarkup(keyboard);
    }

    private SendMessage onPauseChosen(Update update) {
        userService.togglePauseForTelegramUser(update.getMessage().getFrom().getId());
        ReplyKeyboardMarkup keyboard = getSettingsKeyboard(update);

        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(M.getMessage("telegram.message.pause_chosen", null, getLocale(update)))
                .setReplyMarkup(keyboard);
    }

    private SendMessage onResumeChosen(Update update) {
        userService.togglePauseForTelegramUser(update.getMessage().getFrom().getId());
        ReplyKeyboardMarkup keyboard = getSettingsKeyboard(update);

        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(M.getMessage("telegram.message.resume_chosen", null, getLocale(update)))
                .setReplyMarkup(keyboard);
    }

    private SendMessage onStopChosen(Update update) {
        com.dabakovich.entity.User user = userService.getByTelegramId(update.getMessage().getFrom().getId());
        if (user.getSchedule() == null) {
            return new SendMessage()
                    .setChatId(update.getMessage().getChatId())
                    .setText(M.getMessage("telegram.message.settings_menu", null, getLocale(update)))
                    .setReplyMarkup(getSettingsKeyboard(user));
        }
        user.setState(State.STOP_SCHEDULE);
        userService.save(user);
        ReplyKeyboardMarkup keyboard = getYesCancelKeyboard(user);

        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(M.getMessage("telegram.message.stop_chosen", null, getLocale(update)))
                .setReplyMarkup(keyboard);
    }

    private SendMessage onYesChosenForStopSchedule(Update update) {
        userService.stopScheduleByTelegramId(update.getMessage().getFrom().getId());
        com.dabakovich.entity.User user = userService.getByTelegramId(update.getMessage().getFrom().getId());
        user.setState(State.MAIN_MENU);
        userService.save(user);
        ReplyKeyboardMarkup keyboard = getMainMenuKeyboard(user);

        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(M.getMessage("telegram.message.stop_confirmed", null, getLocale(update)))
                .setReplyMarkup(keyboard);
    }

    private SendMessage onLanguageChosen(Update update) {
        com.dabakovich.entity.User user = userService.getByTelegramId(update.getMessage().getFrom().getId());
        user.setState(State.LANGUAGE);
        userService.save(user);
        ReplyKeyboardMarkup keyboard = getLanguagesKeyboard(user);

        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(M.getMessage("telegram.message.language_chosen", null, getLocale(update)))
                .setReplyMarkup(keyboard);
    }

    private SendMessage onSettingCancelChosen(Update update) {
        com.dabakovich.entity.User user = userService.getByTelegramId(update.getMessage().getFrom().getId());
        user.setState(State.MAIN_MENU);
        userService.save(user);
        ReplyKeyboardMarkup keyboard = getMainMenuKeyboard(user);

        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(M.getMessage("telegram.message.cancel_chosen", null, getLocale(update)))
                .setReplyMarkup(keyboard);
    }

    public SendMessage messageOnStartCommand(Update update) {
        Message message = update.getMessage();
        User telegramUser = message.getFrom();

        com.dabakovich.entity.User user = userService.getByTelegramIdOrSave(telegramUser);
        user.setState(State.MAIN_MENU);
        userService.save(user);
        ReplyKeyboardMarkup keyboard = getMainMenuKeyboard(user);
        return sendMessageDefault(update);
    }

    private SendMessage messageOnNewSchedule(Update update, State state) {
        Message message = update.getMessage();
        com.dabakovich.entity.User user = userService.getOne(message.getFrom());
        switch (state) {
            case NEW_SCHEDULER: {
                Schedule schedule = user.getSchedule();
                if (schedule == null) schedule = new Schedule();
                ScheduleType scheduleType;
                try {
                    int scheduleNumber = Integer.valueOf(message.getText()) - 1;
                    String[] scheduleTypes = M.getMessage("telegram.schedulers", null, getLocale(user)).split(",");
                    scheduleType = ScheduleType.valueOf(scheduleTypes[scheduleNumber].split(":")[1]);
                } catch (NumberFormatException e) {
                    scheduleType = null;
                }
                if (scheduleType == null) return sendChooseOptionMessage(update, state);

                schedule.setScheduleType(scheduleType);
                user.setSchedule(schedule);
                user.setState(State.NEW_SCHEDULER_DATE);
                userService.save(user);
                ReplyKeyboardMarkup keyboard = getEnterDateKeyboard(user);
                return new SendMessage()
                        .setChatId(message.getChatId())
                        .setText(M.getMessage("telegram.message.choose_date", null, getLocale(update)))
                        .setReplyMarkup(keyboard);
            }
            case NEW_SCHEDULER_DATE: {
                if (message.getText().startsWith(M.getMessage("telegram.button.another_date", null, getLocale(user)))) {
                    user.setState(State.NEW_SCHEDULER_MANUAL_DATE);
                    userService.save(user);

                    return new SendMessage()
                            .setChatId(message.getChatId())
                            .setText(M.getMessage("telegram.message.enter_date", null, getLocale(update)));
                }

                Schedule schedule = user.getSchedule();
                if (schedule == null) schedule = new Schedule();
                LocalDate startDate;
                if (message.getText().startsWith(M.getMessage("telegram.button.today", null, getLocale(user)))) {
                    startDate = LocalDate.now();
                } else if (message.getText().startsWith(M.getMessage("telegram.button.tomorrow", null, getLocale(user)))) {
                    startDate = LocalDate.now().plusDays(1);
                } else startDate = null;

                if (startDate != null) {
                    schedule.setStartDate(startDate);
                    user.setSchedule(schedule);
                    userService.save(user);
                    return messageOnScheduleCreated(update);
                } else return sendChooseOptionMessage(update, state);
            } case NEW_SCHEDULER_MANUAL_DATE: {
                LocalDate startDate;
                try {
                    startDate = LocalDate.parse(message.getText());
                } catch (DateTimeParseException e) {
                    startDate = null;
                }
                if (startDate != null) {
                    Schedule schedule = user.getSchedule();
                    schedule.setStartDate(startDate);
                    user.setSchedule(schedule);
                    user.setState(State.SEND_CURRENT_PASSAGES_CONFIRM);
                    userService.save(user);
                    return messageOnScheduleCreated(update);
                } else return sendSomeErrorMessage(update, state);
            }
            case SEND_CURRENT_PASSAGES_CONFIRM: {
                if (message.getText().startsWith(M.getMessage("telegram.button.yes", null, getLocale(user)))) {
                    user.setState(State.MAIN_MENU);
                    userService.save(user);
                    return userService.dayPlaneForDateMassage(user, LocalDate.now()).setReplyMarkup(getMainMenuKeyboard(user));
                } else if (message.getText().startsWith(M.getMessage("telegram.button.no", null, getLocale(user)))) {
                    return messageOnMainMenu(update);
                } else return sendChooseOptionMessage(update, state);
            }
            default: return messageOnMainMenu(update);
        }

    }

    private SendMessage messageOnScheduleCreated(Update update) {
        com.dabakovich.entity.User user = userService.getByTelegramId(update.getMessage().getFrom().getId());
        LocalDate nowDate = LocalDate.now();
        if (nowDate.compareTo(user.getSchedule().getStartDate()) >= 0) {
            user.setState(State.SEND_CURRENT_PASSAGES_CONFIRM);
            userService.save(user);
            ReplyKeyboardMarkup keyboard = getYesNoKeyboard(user);
            return new SendMessage()
                    .setChatId(update.getMessage().getChatId())
                    .setText(M.getMessage("telegram.message.send_today_passages", null, getLocale(user)))
                    .setReplyMarkup(keyboard);
        } else return messageOnMainMenu(update);
    }

    private SendMessage sendChooseOptionMessage(Update update, State state) {
        com.dabakovich.entity.User user = userService.getByTelegramId(update.getMessage().getFrom().getId());
        ReplyKeyboardMarkup keyboard;
        switch (state) {
            case NEW_SCHEDULER: {
                keyboard = getSchedulerTypesKeyboard(user);
            }
            break;
            case NEW_SCHEDULER_DATE: {
                keyboard = getEnterDateKeyboard(user);
            }
            break;
            case SEND_CURRENT_PASSAGES_CONFIRM: {
                keyboard = getYesNoKeyboard(user);
            }
            break;
            default: {
                return sendMessageDefault(update);
            }
        }
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(M.getMessage("telegram.message.default_message", null, getLocale(user)))
                .setReplyMarkup(keyboard);
    }

    private SendMessage sendMessageDefault(Update update) {
        com.dabakovich.entity.User user = userService.getByTelegramId(update.getMessage().getFrom().getId());
        user.setState(State.MAIN_MENU);
        userService.save(user);
        ReplyKeyboardMarkup keyboard = getMainMenuKeyboard(user);
        StringBuilder builder = new StringBuilder(M.getMessage("telegram.message.home", null, getLocale(update)));
        if (user.getSchedule() == null) {
            builder.append(M.getMessage("telegram.description.add", null, getLocale(update)));
        }
        builder.append(M.getMessage("telegram.description.settings", null, getLocale(update)));

        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(builder.toString())
                .setParseMode(ParseMode.MARKDOWN)
                .setReplyMarkup(keyboard);
    }

    private SendMessage sendSomeErrorMessage(Update update, State state) {
        switch (state) {
            case NEW_SCHEDULER_MANUAL_DATE: {
                return new SendMessage()
                        .setChatId(update.getMessage().getChatId())
                        .setText(M.getMessage("telegram.message.error", null, getLocale(update)));
            }
            default: return sendMessageDefault(update);
        }
    }

    private ReplyKeyboardMarkup getMainMenuKeyboard(Update update) {
        com.dabakovich.entity.User user = userService.getByTelegramId(update.getMessage().getFrom().getId());
        return getMainMenuKeyboard(user);
    }

    public ReplyKeyboardMarkup getMainMenuKeyboard(com.dabakovich.entity.User user) {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setSelective(true);
        keyboard.setOneTimeKeyboard(false);
        keyboard.setResizeKeyboard(true);
        KeyboardRow row = new KeyboardRow();
        if (user.getSchedule() == null) row.add(M.getMessage("telegram.button.add", null, getLocale(user)));
        row.add(M.getMessage("telegram.button.settings", null, getLocale(user)));
        return keyboard.setKeyboard(Collections.singletonList(row));
    }

    private ReplyKeyboardMarkup getSchedulerTypesKeyboard(Update update) {
        return getSchedulerTypesKeyboard(userService.getByTelegramId(update.getMessage().getFrom().getId()));
    }

    private ReplyKeyboardMarkup getSchedulerTypesKeyboard(com.dabakovich.entity.User user) {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setSelective(true);
        keyboard.setOneTimeKeyboard(true);
        keyboard.setResizeKeyboard(true);
        KeyboardRow row = new KeyboardRow();
        String[] scheduleTypes = M.getMessage("telegram.schedulers", null, getLocale(user)).split(",");
        for (int i = 0; i < scheduleTypes.length; i++) {
            row.add(Integer.toString(i + 1));
        }
        return keyboard.setKeyboard(Collections.singletonList(row));
    }

    private ReplyKeyboardMarkup getEnterDateKeyboard(com.dabakovich.entity.User user) {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setSelective(true);
        keyboard.setOneTimeKeyboard(true);
        keyboard.setResizeKeyboard(true);
        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(M.getMessage("telegram.button.today", null, getLocale(user)));
        firstRow.add(M.getMessage("telegram.button.tomorrow", null, getLocale(user)));
        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add(M.getMessage("telegram.button.another_date", null, getLocale(user)));
        return keyboard.setKeyboard(Arrays.asList(firstRow, secondRow));
    }

    private ReplyKeyboardMarkup getSettingsKeyboard(Update update) {
        return getSettingsKeyboard(userService.getByTelegramId(update.getMessage().getFrom().getId()));
    }

    private ReplyKeyboardMarkup getSettingsKeyboard(com.dabakovich.entity.User user) {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setSelective(true);
        keyboard.setOneTimeKeyboard(false);
        keyboard.setResizeKeyboard(true);
        KeyboardRow row1 = new KeyboardRow();
        if (user.getSchedule() != null) {
            if (user.getSchedule().isActive()) row1.add(M.getMessage("telegram.button.pause", null, getLocale(user)));
            else row1.add(M.getMessage("telegram.button.resume", null, getLocale(user)));
            row1.add(M.getMessage("telegram.button.stop", null, getLocale(user)));
        }
        KeyboardRow row2 = new KeyboardRow();
        row2.add(M.getMessage("telegram.button.language", null, getLocale(user)));
        row2.add(M.getMessage("telegram.button.cancel", null, getLocale(user)));
        return keyboard.setKeyboard(Arrays.asList(row1, row2));
    }

    private ReplyKeyboardMarkup getYesCancelKeyboard(com.dabakovich.entity.User user) {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setSelective(true);
        keyboard.setOneTimeKeyboard(true);
        keyboard.setResizeKeyboard(true);

        KeyboardRow rowYes = new KeyboardRow();
        rowYes.add(M.getMessage("telegram.button.yes", null, getLocale(user)));
        KeyboardRow rowCancel = new KeyboardRow();
        rowYes.add(M.getMessage("telegram.button.cancel", null, getLocale(user)));
        keyboard.setKeyboard(Arrays.asList(rowYes, rowCancel));

        return keyboard;
    }

    private ReplyKeyboardMarkup getYesNoKeyboard(com.dabakovich.entity.User user) {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setSelective(true);
        keyboard.setOneTimeKeyboard(true);
        keyboard.setResizeKeyboard(true);

        KeyboardRow rowYes = new KeyboardRow();
        rowYes.add(M.getMessage("telegram.button.yes", null, getLocale(user)));
        KeyboardRow rowCancel = new KeyboardRow();
        rowYes.add(M.getMessage("telegram.button.no", null, getLocale(user)));
        keyboard.setKeyboard(Arrays.asList(rowYes, rowCancel));

        return keyboard;
    }

    @SuppressWarnings("unchecked")
    private ReplyKeyboardMarkup getLanguagesKeyboard(com.dabakovich.entity.User user) {
        List<String> locales = languageContainer.getLanguages();
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setSelective(true);
        keyboard.setOneTimeKeyboard(true);
        keyboard.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();
        for (String locale : locales) {
            KeyboardRow row = new KeyboardRow();
            row.add(locale.substring(locale.indexOf(":") + 1));
            rows.add(row);
        }
        KeyboardRow cancelRow = new KeyboardRow();
        cancelRow.add(M.getMessage("telegram.button.cancel", null, getLocale(user)));
        rows.add(cancelRow);
        keyboard.setKeyboard(rows);
        return keyboard;
    }

    private Locale getLocale(com.dabakovich.entity.User user) {
        return Locale.forLanguageTag(user.getLanguageTag());
    }

    private Locale getLocale(Update update) {
        com.dabakovich.entity.User user = userService.getByTelegramId(update.getMessage().getFrom().getId());
        if (user != null) {
            return Locale.forLanguageTag(user.getLanguageTag());
        } else return Locale.forLanguageTag("en");
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
