package com.dabakovich.service.impl;

import com.dabakovich.entity.DayPlane;
import com.dabakovich.entity.Group;
import com.dabakovich.entity.User;
import com.dabakovich.repository.DayPlaneRepository;
import com.dabakovich.repository.GroupRepository;
import com.dabakovich.service.GroupService;
import com.dabakovich.service.PushbulletService;
import com.dabakovich.service.TelegramService;
import com.dabakovich.service.UserService;
import com.dabakovich.service.utils.MarkdownStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Created by dabak on 14.08.2017, 16:20.
 */
@Service
public class GroupServiceImpl implements GroupService {

    private MarkdownStringGenerator markdown = new MarkdownStringGenerator();
    private Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);

    private final Environment env;
    private final UserService userService;
    private final GroupRepository groupRepository;
    private final DayPlaneRepository dayPlaneRepository;
    private final PushbulletService pushbulletService;
    private final TelegramService telegramService;
    private final ReloadableResourceBundleMessageSource messageSource;

    @Autowired
    public GroupServiceImpl(Environment env, UserService userService, GroupRepository groupRepository, DayPlaneRepository dayPlaneRepository, PushbulletService pushbulletService, TelegramService telegramService, ReloadableResourceBundleMessageSource messageSource) {
        this.env = env;
        this.userService = userService;
        this.groupRepository = groupRepository;
        this.dayPlaneRepository = dayPlaneRepository;
        this.pushbulletService = pushbulletService;
        this.telegramService = telegramService;
        this.messageSource = messageSource;
    }

    @Override
    public void saveGroup(Group group) {
        for (User user : group.getUsers()) {
            user = userService.getOneOrSave(user);
        }
        groupRepository.save(group);
    }

    @Override
//    @Scheduled(cron = "0 * 16 * * ?")
    public void sendDayPlanes() {
        List<Group> groups = groupRepository.findByActiveIsTrue();
        for (Group group : groups) {
            LocalDate now = LocalDate.now();

        }

        System.out.println("Now is: " + LocalDateTime.now());
    }

    @Override
    public void doDatePush(Group group, LocalDate date) {
        int daysBetween = (int) ChronoUnit.DAYS.between(group.getStartDate(), date);
        DayPlane dayPlane = dayPlaneRepository.findByScheduleTypeAndSequenceNumber(group.getScheduleType(), daysBetween);

        for (User user : group.getUsers()) {
            userService.sendDayPlane(user, dayPlane, date);
//            pushbulletService.pushLink(title, body.toString(), url, user.getEmail());
        }
    }


}
