package com.dabakovich.repository;

import com.dabakovich.entity.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by dabak on 14.08.2017, 16:13.
 */
@Repository
public interface GroupRepository extends MongoRepository<Group, String> {

    List<Group> findByActiveIsTrue();
}
