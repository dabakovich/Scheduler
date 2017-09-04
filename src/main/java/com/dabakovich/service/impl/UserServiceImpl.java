package com.dabakovich.service.impl;

import com.dabakovich.entity.DayPlane;
import com.dabakovich.entity.Passage;
import com.dabakovich.entity.User;
import com.dabakovich.handler.BibleSchedulerBot;
import com.dabakovich.handler.State;
import com.dabakovich.repository.DayPlaneRepository;
import com.dabakovich.repository.UserRepository;
import com.dabakovich.service.TelegramService;
import com.dabakovich.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

/**
 * Created by dabak on 14.08.2017, 18:00.
 */
@Service
public class UserServiceImpl implements UserService {

    private Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final TelegramService telegramService;
    private final BibleSchedulerBot bibleSchedulerBot;
    private final DayPlaneRepository dayPlaneRepository;
    private final ReloadableResourceBundleMessageSource messageSource;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, TelegramService telegramService, BibleSchedulerBot bibleSchedulerBot, DayPlaneRepository dayPlaneRepository, ReloadableResourceBundleMessageSource messageSource) {
        this.userRepository = userRepository;
        this.telegramService = telegramService;
        this.bibleSchedulerBot = bibleSchedulerBot;
        this.dayPlaneRepository = dayPlaneRepository;
        this.messageSource = messageSource;
    }

    @PostConstruct
    public void setUserServiceForBibleSchedulerBot() {
        bibleSchedulerBot.setUserService(this);
//        User user = userRepository.findByUserName("Світлана Табака");
//        logger.info(user.getUserName());
//        System.out.println(user.getUserName());
//        System.out.println(Objects.equals(user.getUserName(), "Світлана Табака"));
//        System.out.println("Світлана Табака");
    }

    @Scheduled(cron = "0 0 7 * * ?")
    public void sendDayPlanes() {
        LocalDate nowDate = LocalDate.now();
        List<User> usersWithSchedule = userRepository.findByScheduleIsNotNull();
        logger.info("Started daily scheduling...");
//        System.out.println("YES!");
        usersWithSchedule
                .stream()
                .filter(user -> user.getSchedule().isActive())
                .forEach(user -> sendDayPlaneForDateMessage(user, nowDate));
        usersWithSchedule
                .stream()
                .filter(user -> !user.getSchedule().isActive())
                .peek(User::moveStartDateOneDayAhead)
                .forEach(userRepository::save);
    }

    private void sendDayPlaneForDateMessage(User user, LocalDate date) {
        telegramService.sendMessage(dayPlaneForDateMassage(user, date));
        logger.info("Sent daily message for user " + user.getUserName());
    }

    @Override
    public User getOne(org.telegram.telegrambots.api.objects.User telegramUser) {
        return userRepository.findByTelegramId(telegramUser.getId());
    }

    @Override
    public User getByTelegramId(Integer telegramId) {
        return userRepository.findByTelegramId(telegramId);
    }

    @Override
    public User getByTelegramIdOrSave(org.telegram.telegrambots.api.objects.User telegramUser) {
        int telegramId = telegramUser.getId();
        User user = userRepository.findByTelegramId(telegramId);
        if (user != null) return user;
        user = new User();
        String userName = telegramUser.getUserName() == null ? telegramUser.getFirstName() + " " + telegramUser.getLastName() : telegramUser.getUserName();
        user.setTelegramId(telegramId);
        user.setUserName(userName);
        user.setLanguageTag(telegramUser.getLanguageCode());

        return userRepository.save(user);
    }

    @Override
    public boolean togglePauseForTelegramUser(Integer telegramId) {
        User user = userRepository.findByTelegramId(telegramId);
        if (user.getSchedule() != null) {
            if (user.getSchedule().isActive()) {
                user.getSchedule().setActive(false);
            } else {
                user.getSchedule().setActive(true);
            }
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public State getState(Integer telegramId) {
        User user = userRepository.findByTelegramId(telegramId);
        return user != null ? user.getState() : State.START_STATE;
    }

    @Override
    public void setState(Integer telegramId, State state) {
        User user = userRepository.findByTelegramId(telegramId);
        user.setState(state);
        userRepository.save(user);
    }

    @Override
    public SendMessage dayPlaneForDateMassage(User user, LocalDate date) {
        if (user.getSchedule().getStartDate() == null) return null;
        int daysBetween = (int) ChronoUnit.DAYS.between(user.getSchedule().getStartDate(), date);
        DayPlane dayPlane = dayPlaneRepository.findByScheduleTypeAndSequenceNumber(user.getSchedule().getScheduleType(), daysBetween);
        return dayPlaneForDateMassage(user, dayPlane, date);
    }

    @Override
    public SendMessage dayPlaneForDateMassage(User user, DayPlane dayPlane, LocalDate date) {
        Locale locale = Locale.forLanguageTag(user.getLanguageTag());
        StringBuilder body = new StringBuilder(messageSource.getMessage("telegram.title", null, locale) + ". " + date.format(DateTimeFormatter.ofPattern("dd.MM.yy")) + "\n");
        String url = getUrl(dayPlane.getPassages(), locale);
        body.append(messageSource.getMessage("telegram.text", new Object[]{url}, locale));

        for (Passage passage : dayPlane.getPassages()) {
            body.append(messageSource.getMessage("book." + passage.getBook(), null, locale))
                    .append(" ").append(passage.getVerses()).append("\n");
        }

        ReplyKeyboardMarkup keyboard = bibleSchedulerBot.getMainMenuKeyboard(user);
        return new SendMessage()
                .setChatId((long) user.getTelegramId())
                .setText(body.toString())
                .setParseMode("Markdown")
                .setReplyMarkup(keyboard)
                .disableWebPagePreview();
    }

    @Override
    public void stopScheduleByTelegramId(Integer telegramId) {
        User user = userRepository.findByTelegramId(telegramId);
        user.setSchedule(null);
        userRepository.save(user);
    }

    @Override
    public void setLanguageByTelegramId(Integer telegramId, String messageText) {
        User user = userRepository.findByTelegramId(telegramId);
        user.setLanguageTag(messageText.substring(0, messageText.indexOf(":")));
        userRepository.save(user);
    }

    @Override
    public User getOneOrSave(User user) {
        if (user.getId() != null) return userRepository.findOne(user.getId());
        else return userRepository.save(user);
    }

    @Override
    public void setLocaleForUser(String id, String locale) {
        User user = userRepository.findOne(id);
        user.setLanguageTag(locale);
        userRepository.save(user);
    }

    private String getUrl(List<Passage> passages, Locale locale) {
        String url = messageSource.getMessage("wol.url.search", null, locale);
        StringBuilder parameters = new StringBuilder();
        int last = passages.size() - 1;
        for (int i = 0; i < passages.size(); i++) {
            parameters.append(messageSource.getMessage("book.short." + passages.get(i).getBook(), null, locale))
                    .append(" ")
                    .append(passages.get(i).getVerses());
            if (i < last) parameters.append("; ");
        }
        try {
            return url + "?q=" + URLEncoder.encode(parameters.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return url + "?q=" + parameters.toString();
        }
    }
}
