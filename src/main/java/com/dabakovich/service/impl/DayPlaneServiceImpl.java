package com.dabakovich.service.impl;

import com.dabakovich.entity.DayPlane;
import com.dabakovich.repository.DayPlaneRepository;
import com.dabakovich.service.DayPlaneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dabak on 15.08.2017, 13:46.
 */
@Service
public class DayPlaneServiceImpl implements DayPlaneService {

    private final DayPlaneRepository dayPlaneRepository;

    @Autowired
    public DayPlaneServiceImpl(DayPlaneRepository dayPlaneRepository) {
        this.dayPlaneRepository = dayPlaneRepository;
    }

    @Override
//    @Profiling
    public void saveOrUpdateDayPlane(DayPlane dayPlane) {
        DayPlane dp = dayPlaneRepository.findByScheduleTypeAndSequenceNumber(dayPlane.getScheduleType(), dayPlane.getSequenceNumber());
        if (dp != null) {
            dayPlane.setId(dp.getId());
        }
        dayPlaneRepository.save(dayPlane);
    }
}
