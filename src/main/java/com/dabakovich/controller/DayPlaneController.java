package com.dabakovich.controller;

import com.dabakovich.entity.DayPlane;
import com.dabakovich.service.DayPlaneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dabak on 14.08.2017, 18:10.
 */
@RestController
@RequestMapping("/day-plane")
public class DayPlaneController {

    private final DayPlaneService dayPlaneService;

    @Autowired
    public DayPlaneController(DayPlaneService dayPlaneService) {
        this.dayPlaneService = dayPlaneService;
    }

    @PutMapping
    public void addDayPlane(@RequestBody DayPlane dayPlane) {
        dayPlaneService.saveOrUpdateDayPlane(dayPlane);
    }
}
