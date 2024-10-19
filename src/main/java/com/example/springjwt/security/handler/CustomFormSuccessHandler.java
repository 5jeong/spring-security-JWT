package com.example.springjwt.security.handler;

import com.example.springjwt.dto.UserContext;
import com.example.springjwt.jwt.JWTUtil;
import com.example.springjwt.jwt.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomFormSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        UserContext userContext = (UserContext) authentication.getPrincipal();
        String username = userContext.getUsername();

        Collection<? extends GrantedAuthority> authorities = userContext.getAuthorities();
        GrantedAuthority auth = authorities.iterator().next();
        String role = auth.getAuthority();

        // 로그인 성공 시, 새로운 엑세스 토큰과 리프레시 토큰 생성
        String accessToken = jwtUtil.createAccessToken(username, role);
        String refreshToken = jwtUtil.createRefreshToken(username);

        // 리프레시 토큰을 레디스에 저장
        refreshTokenService.saveRefreshToken(username, refreshToken);

        // 응답 헤더에 엑세스 토큰 추가
        response.setHeader("Authorization", "Bearer " + accessToken);

        // 쿠키에 리프레시 토큰 추가
        response.addCookie(refreshTokenService.createCookie("RefreshToken", refreshToken));
        response.setStatus(HttpStatus.OK.value());
    }
}
