package com.dabakovich.repository;

import com.dabakovich.entity.DayPlane;
import com.dabakovich.entity.ScheduleType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by dabak on 14.08.2017, 16:04.
 */
@Repository
public interface DayPlaneRepository extends MongoRepository<DayPlane, String> {

    DayPlane findByScheduleTypeAndSequenceNumber(ScheduleType scheduleType, int sequenceNumber);
}
