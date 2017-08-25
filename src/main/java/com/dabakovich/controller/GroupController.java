package com.dabakovich.controller;

import com.dabakovich.entity.Group;
import com.dabakovich.repository.GroupRepository;
import com.dabakovich.service.GroupService;
import com.dabakovich.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Created by dabak on 14.08.2017, 16:10.
 */
@RestController
@RequestMapping("/group")
public class GroupController {

    private Logger logger = LoggerFactory.getLogger(GroupController.class);

    private final GroupService groupService;
    private final GroupRepository groupRepository;
    private final UserService userService;

    @Autowired
    public GroupController(GroupService groupService, GroupRepository groupRepository, UserService userService) {
        this.groupService = groupService;
        this.groupRepository = groupRepository;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public Group getGroup(@PathVariable("id") String id) {
        Group group = groupRepository.findOne(id);
//        group.setStartDate(LocalDate.of(2017, 1, 1));
        return group;
    }

    @GetMapping("/{id}/push")
    public void doPush(@PathVariable("id") String id) {
        Group group = groupRepository.findOne(id);
        groupService.doDatePush(group, LocalDate.now().minusDays(1));
    }

    @PutMapping
    public void createGroup(@RequestBody Group group) {
        groupService.saveGroup(group);
    }

    @PostMapping
    public void updateGroup(@RequestBody Group group) {
        groupService.saveGroup(group);
    }

}
