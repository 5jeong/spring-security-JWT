package com.example.springjwt.security.config;

import com.example.springjwt.jwt.CustomLogoutFilter;
import com.example.springjwt.jwt.JWTFilter;
import com.example.springjwt.jwt.JWTUtil;
import com.example.springjwt.jwt.LoginFilter;
import com.example.springjwt.jwt.RefreshTokenService;
import com.example.springjwt.security.handler.CustomAuthenticationFailureHandler;
import com.example.springjwt.security.handler.CustomAuthenticationSuccessHandler;
import com.example.springjwt.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity // Spring Security를 활성화합니다.
@RequiredArgsConstructor
public class SecurityConfig {

    // AuthenticationConfiguration: Spring Security에서 인증에 대한 설정을 관리하는 클래스입니다.
    private final AuthenticationConfiguration authenticationConfiguration;
    private final CustomAuthenticationSuccessHandler successHandler;
    private final CustomAuthenticationFailureHandler failureHandler;
    private final CustomUserDetailsService customUserDetailsService;
    private final RefreshTokenService refreshTokenService;

    // JWTUtil: JWT 토큰의 생성과 검증을 처리하는 유틸리티 클래스입니다. 이 설정 클래스에서 사용됩니다.
    private final JWTUtil jwtUtil;

    // 비밀번호 암호화 객체를 Bean으로 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        // Spring Security의 인증을 처리하는 AuthenticationManager를 빈으로 등록합니다.
        return configuration.getAuthenticationManager();
    }

    // SecurityFilterChain: Spring Security의 HTTP 보안 설정을 담당하는 메서드입니다.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // CORS 설정
        http
                .cors((cors) -> cors
                        .configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration configuration = new CorsConfiguration();
                                // 프론트엔드 도메인에서 요청을 허용
                                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                                // 모든 HTTP 메서드를 허용 (GET, POST, PUT, DELETE 등)
                                configuration.setAllowedMethods(Collections.singletonList("*"));
                                // 쿠키를 포함한 인증 정보를 허용
                                configuration.setAllowCredentials(true);
                                // 모든 헤더를 허용
                                configuration.setAllowedHeaders(Collections.singletonList("*"));
                                // CORS 응답 캐시 시간 설정 (1시간)
                                configuration.setMaxAge(3600L);

                                configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
                                // 응답 헤더 중 Authorization을 클라이언트에서 확인 가능하게 노출
                                configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                                return configuration;
                            }
                        }));

        // CSRF 비활성화 (jwt 토큰 인증방식이기 때문에 비활성화)
        http
                .csrf(AbstractHttpConfigurer::disable);

        // Form 로그인 방식 비활성화 (로그인 페이지를 사용하지 않음)
        http
                .formLogin((auth) -> auth.disable());

        // 기본 HTTP Basic 인증 비활성화 (헤더에 사용자명과 비밀번호를 노출하는 인증 방식)
        http
                .httpBasic((auth) -> auth.disable());

        // 요청별 권한 설정
        http
                .authorizeHttpRequests((auth) -> auth
                        // 로그인, 메인 페이지, 회원가입 요청은 누구나 접근 가능
                        .requestMatchers("/login", "/member/**","/auth/reissue").permitAll()
                        // /admin 경로는 ADMIN 권한이 있는 사용자만 접근 가능
                        .requestMatchers("/admin").hasRole("ADMIN")
                        // 그 외의 모든 요청은 인증된 사용자만 접근 가능
                        .anyRequest().authenticated());

        // JWTFilter 추가 (JWT 검증 필터). JWT를 사용하여 토큰 검증을 하기 위한 필터를 등록
        http
                .addFilterBefore(new JWTFilter(jwtUtil, customUserDetailsService),
                        LoginFilter.class);

        // LoginFilter 추가 (로그인 요청 처리 필터). 로그인 요청 시 JWT를 발급하는 필터를 등록
        LoginFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration));
        loginFilter.setAuthenticationSuccessHandler(successHandler);
        loginFilter.setAuthenticationFailureHandler(failureHandler);

        http
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshTokenService), LogoutFilter.class);

        // 세션 관리를 Stateless로 설정 (세션을 사용하지 않음). JWT는 세션 기반 인증을 사용하지 않으므로 이렇게 설정
        http
                .sessionManagement((session) ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 최종적으로 SecurityFilterChain을 반환하여 Spring Security에서 설정을 사용하도록 합니다.
        return http.build();
    }
}

