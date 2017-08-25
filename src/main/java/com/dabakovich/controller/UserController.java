package com.dabakovich.controller;

import com.dabakovich.entity.User;
import com.dabakovich.repository.UserRepository;
import com.dabakovich.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by dabak on 14.08.2017, 17:30.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PutMapping
    public void addUser(@RequestBody User user) {
        userRepository.save(user);
    }

    @PostMapping
    public void updateUser(@RequestBody User user) {
        userRepository.save(user);
    }

    @GetMapping("/{id}/{locale}")
    public void setLocale(@PathVariable("id") String id, @PathVariable("locale") String locale) {
        userService.setLocaleForUser(id, locale);
    }
}
