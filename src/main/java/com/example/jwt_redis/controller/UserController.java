package com.example.jwt_redis.controller;

import com.example.jwt_redis.controller.request.UserJoinRequest;
import com.example.jwt_redis.controller.request.UserLoginRequest;
import com.example.jwt_redis.controller.response.Response;
import com.example.jwt_redis.controller.response.UserJoinResponse;
import com.example.jwt_redis.controller.response.UserLoginResponse;
import com.example.jwt_redis.model.User;
import com.example.jwt_redis.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/join") // 회원 가입
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
        User user = userService.join(request.getName(), request.getPassword());
        UserJoinResponse response = UserJoinResponse.fromUser(user);
        return Response.success(response);
    }

    @PostMapping("/login") // 로그인
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        String token = userService.login(request.getName(), request.getPassword());
        return Response.success(new UserLoginResponse(token));
    }
}
