package com.example.jwt_redis.service;

import com.example.jwt_redis.exception.ErrorCode;
import com.example.jwt_redis.exception.JwtRedisAppException;
import com.example.jwt_redis.model.User;
import com.example.jwt_redis.model.entity.UserEntity;
import com.example.jwt_redis.repository.UserCacheRepository;
import com.example.jwt_redis.repository.UserEntityRepository;
import com.example.jwt_redis.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserEntityRepository userEntityRepository;
    private final BCryptPasswordEncoder encoder;
    private final UserCacheRepository userCacheRepository;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTimeMs;

    public User loadUserByUserName(String userName){
        return userCacheRepository.getUser(userName).orElseGet(() -> // 캐시에 값이 없으면 아래 내용을 실행해서 db에서 유저를 가져온다.
                userEntityRepository.findByUserName(userName).map(User::fromEntity).orElseThrow( // 캐시에 값이 있으면 캐시에서 가져온다.
                        () -> new JwtRedisAppException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)))
        );
    }

    @Transactional  // Exception이 발생하면 엔티티를 저장하는 부분이 rollback이 될 수 있다.
    public User join(String userName, String password){
        // 회원가입하려는 userName으로 회원가입된 user가 있는지
        // user가 있다면 에러를 발생시킨다.
        userEntityRepository.findByUserName(userName).ifPresent(it -> {
            throw new JwtRedisAppException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", userName));
        });

        // 회원가입 진행 = user를 등록
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName, encoder.encode(password)));
//        throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", userName));
//        throw new RuntimeException(); // save 이후에도 @Transactional이 있기 때문에 에러 발생하면 db에 저장안된다.
        return User.fromEntity(userEntity);
    }

    // 로그인 성공하면 그에 맞는 토큰을 반환
    public String login(String userName, String password){
        // 회원가입 여부 체크, 없으면 Throw 던져줌
//        UserEntity userEntity = userEntityRepository.findByUserName(userName).
//                orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
        // loadUserByUserName 해서 불러오고 캐싱을하자
        User user = loadUserByUserName(userName);
        userCacheRepository.setUser(user);  // 로그인 했을 때 캐싱을한다.

        // 비밀번호 체크
        // rawpassword와 encodedpassword가 매치가 안되면 에러 발생시킨다.
        if(!encoder.matches(password, user.getPassword())) {
            //if(!userEntity.getPassword().equals(password)){
            throw new JwtRedisAppException(ErrorCode.INVALID_PASSWORD);
        }

        // 토큰 생성
        return JwtTokenUtils.generateToken(userName, secretKey, expiredTimeMs);
    }
}
