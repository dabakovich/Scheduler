package com.dabakovich.controller;

import com.dabakovich.entity.DayPlane;
import com.dabakovich.entity.ScheduleType;
import com.dabakovich.entity.User;
import com.dabakovich.repository.DayPlaneRepository;
import com.dabakovich.repository.UserRepository;
import com.dabakovich.service.PushbulletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scheduler")
public class MainController {

    private final UserRepository userRepository;

    private final DayPlaneRepository dayPlaneRepository;

    private final PushbulletService pushbulletService;

    @Autowired
    public MainController(UserRepository userRepository, DayPlaneRepository dayPlaneRepository, PushbulletService pushbulletService) {
        this.userRepository = userRepository;
        this.dayPlaneRepository = dayPlaneRepository;
        this.pushbulletService = pushbulletService;
    }

    @GetMapping("/send/{message}")
    public void sendMessage(@PathVariable("message") String message) {
        pushbulletService.pushNote("Message", message, "dabakovich@gmail.com");
    }

    @GetMapping("/{name}")
    public User getUser(@PathVariable("name") String name) {
        return userRepository.findByName(name);
    }

    @PostMapping
    public void addUser(@RequestBody User user) {
        userRepository.save(user);
    }

    @GetMapping("/passages")
    public DayPlane getPlane() {
        DayPlane dayPlane = new DayPlane();
        dayPlane.setSequenceNumber(1);
        dayPlane.setScheduleType(ScheduleType.CHRONOLOGICAL);
        return dayPlane;
    }

    @PostMapping("/passages")
    public void addPassages(@RequestBody List<DayPlane> passages) {
//        System.out.println(passages);

        dayPlaneRepository.save(passages);
    }
}
