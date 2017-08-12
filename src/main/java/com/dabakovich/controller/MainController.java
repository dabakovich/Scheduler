package com.dabakovich.controller;

import com.dabakovich.entity.User;
import com.dabakovich.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scheduler")
public class MainController {

    private final UserRepository userRepository;

    @Autowired
    public MainController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{name}")
    public User getUser(@PathVariable("name") String name) {
        return userRepository.findByName(name);
    }

    @PostMapping
    public void addUser(@RequestBody User user) {
        userRepository.save(user);
    }
}
