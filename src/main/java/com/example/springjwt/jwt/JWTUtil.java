package com.example.springjwt.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.PushBuilder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil {
    private SecretKey secretKey;
    @Value("${token.access-token-expiration}")
    private int accessTokenExpiration; // 10분
    @Value("${token.refresh-token-expiration}")
    private int refreshTokenExpiration; // 7일

    // 생성자: 외부 설정 파일에서 JWT 서명용 시크릿 키를 받아서 SecretKey로 변환
    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        // 전달받은 문자열을 바이트 배열로 변환하여 SecretKeySpec 객체로 생성
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                SIG.HS256.key().build().getAlgorithm()); // HS256 알고리즘 사용
    }

    // JWT 토큰에서 'username' 클레임을 추출
    public String getUsername(String token) {
        // JWT 토큰을 파싱하고 서명 검증을 수행한 후, 'username' 클레임 값을 String으로 반환
        return Jwts.parser()
                .verifyWith(secretKey) // 시크릿 키로 서명을 검증
                .build()
                .parseSignedClaims(token) // 토큰을 파싱하여 클레임들을 추출
                .getPayload() // 페이로드(내용)에서 클레임 정보 추출
                .get("username", String.class); // 'username' 클레임을 String 타입으로 반환
    }

    // JWT 토큰에서 'role' 클레임을 추출
    public String getRole(String token) {
        // JWT 토큰을 파싱하고 서명 검증을 수행한 후, 'role' 클레임 값을 String으로 반환
        return Jwts.parser()
                .verifyWith(secretKey) // 시크릿 키로 서명을 검증
                .build()
                .parseSignedClaims(token) // 토큰을 파싱하여 클레임들을 추출
                .getPayload() // 페이로드(내용)에서 클레임 정보 추출
                .get("role", String.class); // 'role' 클레임을 String 타입으로 반환
    }

    // 토큰의 만료 시간을 확인하는 메서드
    public Boolean isExpired(String token) {

        // 만료 시간을 확인하고 현재 시간보다 이전이면 true 반환
        return Jwts.parser()
                .verifyWith(secretKey) // 시크릿 키로 서명을 검증
                .build()
                .parseSignedClaims(token) // 토큰을 파싱하여 클레임들을 추출
                .getPayload() // 페이로드에서 클레임 정보 추출
                .getExpiration()
                .before(new Date());
    }

    // JWT 토큰 생성 메서드
    public String createAccessToken(String username, String role) {
        // JWT 토큰을 생성하고 클레임과 만료 시간을 설정하여 반환
        return Jwts.builder()
                .claim("username", username) // 'username' 클레임 설정
                .claim("role", role) // 'role' 클레임 설정
                .issuedAt(new Date(System.currentTimeMillis())) // 토큰 발급 시간 설정
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration)) // 만료 시간 설정
                .signWith(secretKey) // 시크릿 키로 서명을 설정
                .compact(); // 최종적으로 JWT 토큰을 문자열로 반환
    }

    // 리프레시 토큰 생성 메서드
    public String createRefreshToken(String username) {
        return Jwts.builder()
                .claim("username", username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(secretKey)
                .compact();
    }

    // 쿠키에서 리프레시 토큰 추출
    public String extractRefreshToken(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("RefreshToken"))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

}
