package com.dabakovich.service.impl;

import com.dabakovich.entity.DayPlane;
import com.dabakovich.entity.Passage;
import com.dabakovich.entity.User;
import com.dabakovich.entity.telegram.MessageToSend;
import com.dabakovich.repository.UserRepository;
import com.dabakovich.service.TelegramService;
import com.dabakovich.service.UserService;
import com.dabakovich.service.utils.MarkdownStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Created by dabak on 14.08.2017, 18:00.
 */
@Service
public class UserServiceImpl implements UserService {

    private MarkdownStringGenerator markdown = new MarkdownStringGenerator();

    private final UserRepository userRepository;
    private final TelegramService telegramService;
    private final ReloadableResourceBundleMessageSource messageSource;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, TelegramService telegramService, ReloadableResourceBundleMessageSource messageSource) {
        this.userRepository = userRepository;
        this.telegramService = telegramService;
        this.messageSource = messageSource;
    }

    @Override
    public User getByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public void sendDayPlane(User user, DayPlane dayPlane, LocalDate date) {
        Locale locale = Locale.forLanguageTag(user.getLanguageTag());
        String title = messageSource.getMessage("telegram.title", null, locale) + ". " + date.format(DateTimeFormatter.ofPattern("dd.MM.yy")) + "\n";
        String url = getUrl(dayPlane.getPassages(), locale);
        StringBuilder body = new StringBuilder(messageSource.getMessage("telegram.text", new Object[]{url}, locale));

        for (Passage passage : dayPlane.getPassages()) {
            body.append(messageSource.getMessage("book." + passage.getBook(), null, locale))
                    .append(" ").append(passage.getVerses()).append("\n");
        }
        MessageToSend message = new MessageToSend(user.getChatId(), markdown.bold(title) + body.toString());
        message.setParseMode("Markdown");
        message.setDisableWebPagePreview(true);
        telegramService.sendMessage(message);
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
