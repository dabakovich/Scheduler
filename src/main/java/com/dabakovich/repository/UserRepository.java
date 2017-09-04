package com.dabakovich.repository;

import com.dabakovich.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    User findByTelegramId(int telegramId);

    User findByUserName(String userName);

    List<User> findByScheduleIsNotNull();
}
