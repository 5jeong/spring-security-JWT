package com.example.springjwt.oauth2.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public class OAuth2HeaderService {

    public String oauth2JwtHeaderSet(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String accessToken = null;

        // 쿠키가 없으면 400 Bad Request 응답 반환
        if (cookies == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "No cookies found";
        }

        // 쿠키에서 "access" 토큰 찾기
        for (Cookie cookie : cookies) {
            if ("Authorization".equals(cookie.getName())) {
                accessToken = cookie.getValue();
                break;
            }
        }

        // Access Token이 없으면 400 Bad Request 응답 반환
        if (accessToken == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "Access token not found in cookies";
        }

        // Access Token 쿠키 만료 (HttpOnly와 Secure 설정 유지)
        Cookie expiredCookie = new Cookie("Authorization", null);
        expiredCookie.setMaxAge(0);
        expiredCookie.setPath("/");
        expiredCookie.setHttpOnly(true);
        response.addCookie(expiredCookie);

        // Authorization 헤더에 Bearer 토큰 설정
        response.addHeader("Authorization", "Bearer " + accessToken);
        response.setStatus(HttpServletResponse.SC_OK);

        return "Access token moved to header";
    }
}
