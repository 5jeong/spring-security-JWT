package com.example.springjwt.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    private static final String LOGOUT_URI = "/logout";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 로그아웃 경로와 HTTP 메서드 검증
        if (!isLogoutRequest(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        // 리프레시 토큰 추출
        String refreshToken = jwtUtil.extractRefreshToken(httpRequest);
        if (refreshToken == null) {
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 리프레시 토큰 유효성 검사 및 만료 확인
        if (isTokenInvalid(refreshToken, httpResponse)) return;

        String username = jwtUtil.getUsername(refreshToken);

        // 레디스에 저장된 리프레시 토큰 확인
        if (refreshTokenService.getRefreshToken(username) == null) {
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 로그아웃 처리 - 리프레시 토큰 삭제 및 쿠키 무효화
        performLogout(username, httpResponse);
    }

    // 로그아웃 요청인지 확인
    private boolean isLogoutRequest(HttpServletRequest request) {
        return LOGOUT_URI.equals(request.getRequestURI()) && "POST".equalsIgnoreCase(request.getMethod());
    }


    // 리프레시 토큰 유효성 검사 및 만료 여부 확인
    private boolean isTokenInvalid(String refreshToken, HttpServletResponse response) throws IOException {
        try {
            jwtUtil.isExpired(refreshToken);
            return false;
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return true;
        }
    }

    // 로그아웃 처리: 리프레시 토큰 삭제 및 쿠키 무효화
    private void performLogout(String username, HttpServletResponse response) throws IOException {
        // 리프레시 토큰 삭제
        refreshTokenService.deleteRefreshToken(username);

        // 리프레시 토큰 쿠키 만료 설정
        refreshTokenService.deleteRefreshCookie(response);
        PrintWriter writer = response.getWriter();
        writer.print("로그아웃 성공 ");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
