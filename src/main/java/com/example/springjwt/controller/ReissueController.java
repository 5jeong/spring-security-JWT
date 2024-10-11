package com.example.springjwt.controller;

import com.example.springjwt.dto.TokenResponse;
import com.example.springjwt.jwt.JWTUtil;
import com.example.springjwt.jwt.RefreshTokenService;
import com.example.springjwt.jwt.exception.TokenException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    // 엑세스토큰이 만료될때만 프론트가 요청
    @PostMapping("/reissue")
    public ResponseEntity<?> reissueToken(HttpServletRequest request, HttpServletResponse response) {


        try {
            // 쿠키에서 리프레시 토큰 가져오기
            String refreshToken = jwtUtil.extractRefreshToken(request);

            // Access Token은 만료됐지만, Refresh Token은 유효한 경우
            TokenResponse tokenResponse = refreshTokenService.reissueTokens(refreshToken);

            // 응답 헤더에 새로운 엑세스 토큰 추가
            response.setHeader("Authorization", "Bearer " + tokenResponse.getAccessToken());

            // 기존 쿠키 삭제
            refreshTokenService.deleteRefreshCookie(response);
            // 쿠키에 새로운 리프레시 토큰 설정
            Cookie refreshTokenCookie = refreshTokenService.createCookie("RefreshToken",
                    tokenResponse.getRefreshToken());
            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok("토큰 재발급 성공");
        } catch (TokenException e) {
            // Access Token과 Refresh Token 모두 만료된 경우
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
