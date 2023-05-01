package com.example.jwt_redis.configuration;

import com.example.jwt_redis.configuration.filter.JwtTokenFilter;
import com.example.jwt_redis.exception.CustomAuthenticationEntryPoint;
import com.example.jwt_redis.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthenticationConfig extends WebSecurityConfigurerAdapter {
    private final UserService userService;
    @Value("${jwt.secret-key}")
    private String key;

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 해당 정규 표현식에 해당하는 것만 통과시키고 그게 아닌 것들은 ignore 한다.
        web.ignoring().regexMatchers("^(?!/api/).*")
                .antMatchers("/api/*/users/join", "/api/*/users/login");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests()
//                .antMatchers().permitAll() // 해당 주소 허용
                .antMatchers("/api/**").authenticated() // /api/** 경로를 authenticated 해준다
                .and()
                .sessionManagement()    // 세션은 따로 관리 안할거다.
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()  // http request됐을 때 시큐리티 설정 - 필터를 둬서 들어온 토큰이 어떤 유저가 들어왔는지 토큰을 확인
                .addFilterBefore(new JwtTokenFilter(key, userService), UsernamePasswordAuthenticationFilter.class) // 얘가 JwtTokenFilter 가게 해줬군
                .exceptionHandling()    // 예외처리 기능이 작동
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint());// 필터에서 에러가 났을 경우에 exceptionHandling 한다음에 EntryPoint로 보내줘야한다.
    }
}
