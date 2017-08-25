package com.dabakovich.service;

import com.dabakovich.entity.Group;

import java.time.LocalDate;

/**
 * Created by dabak on 14.08.2017, 16:19.
 */
public interface GroupService {

    void saveGroup(Group group);

    void sendDayPlanes();

    void doDatePush(Group group, LocalDate date);
}
