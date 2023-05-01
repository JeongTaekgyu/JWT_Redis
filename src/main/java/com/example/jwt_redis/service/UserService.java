package com.example.jwt_redis.service;

import com.example.jwt_redis.model.User;

public interface UserService {
    User loadUserByUserName(String userName);
    User join(String userName, String password);
    String login(String userName, String password);
}
