package com.example.jwt_redis.repository;

import com.example.jwt_redis.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserCacheRepository {

    private final RedisTemplate<String, User> userRedisTemplate;
    private final static Duration USER_CACHE_TTL = Duration.ofDays(3); // 캐시 만료시간 3일

    public void setUser(User user){
        String key = getKey(user.getUsername());
        log.info("Set User to Redis {}, {}", key, user);
        // opsForValue는 Strings를 쉽게 Serialize / Deserialize 해주는 Interface 이다.
        userRedisTemplate.opsForValue().set(key, user, USER_CACHE_TTL);
    }

    public Optional<User> getUser(String userName){
        String key = getKey(userName);
        User user = userRedisTemplate.opsForValue().get(key);
        log.info("Get data from Redis {} , {}", key, user);
        return Optional.ofNullable(user);
    }

    private String getKey(String userName){
        // 어느 것의 userName인지 알아야 해서 prefix값을 준다.
        return "USER:" + userName;
    }
}
