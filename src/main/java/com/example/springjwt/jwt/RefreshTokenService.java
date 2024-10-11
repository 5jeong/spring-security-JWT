package com.example.springjwt.jwt;


import com.example.springjwt.dto.TokenResponse;
import com.example.springjwt.entity.UserEntity;
import com.example.springjwt.jwt.exception.TokenException;
import com.example.springjwt.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${token.refresh-token-expiration}")
    private int refreshTokenExpiration; // 7일

    // 리프레시 토큰 저장
    public void saveRefreshToken(String username, String refreshToken) {
        redisTemplate.opsForValue().set(username, refreshToken, refreshTokenExpiration, TimeUnit.MILLISECONDS);
    }

    // 리프레시 토큰 조회
    public String getRefreshToken(String username) {
        // 레디스에서 사용자 이름으로 리프레시 토큰 가져옴
        return redisTemplate.opsForValue().get(username);
    }


    // 리프레시 토큰 검증
    public boolean validateRefreshToken(String username, String refreshToken) {
        String storedToken = getRefreshToken(username);
        return storedToken != null && storedToken.equals(refreshToken);
    }

    // 리프레시 토큰 삭제 (로그아웃 시)
    public void deleteRefreshToken(String username) {
        redisTemplate.delete(username);
    }

    //리프레시 토큰 쿠키 생성
    public Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7일을 초 단위로 설정
        cookie.setHttpOnly(true);
        return cookie;
    }

    // 리프레시 토큰 쿠키 삭제
    public void deleteRefreshCookie(HttpServletResponse response) {
        Cookie deleteCookie = new Cookie("RefreshToken", null);
        deleteCookie.setMaxAge(0);
        deleteCookie.setPath("/");
        deleteCookie.setHttpOnly(true);
        response.addCookie(deleteCookie);
    }


    // 리프레시 토큰 재발급 처리
    public TokenResponse reissueTokens(String refreshToken){
        if (refreshToken == null) {
            throw new TokenException("리프레시 토큰이 비어있습니다.");
        }

        if(jwtUtil.isExpired(refreshToken)){
            throw new TokenException("리프레시 토큰이 만료되었습니다.");
        }
        String username = jwtUtil.getUsername(refreshToken);

        if (!validateRefreshToken(username, refreshToken)) {
            throw new TokenException("리프레시 토큰이 Redis에 존재하지 않습니다.");
        }

        // 새로운 엑세스 토큰 및 리프레시 토큰 발급
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        String newAccessToken = jwtUtil.createAccessToken(username, user.getRole());
        String newRefreshToken = jwtUtil.createRefreshToken(username);

        // 기존 리프레시 토큰 삭제
        deleteRefreshToken(username);
        saveRefreshToken(username,refreshToken);

        return new TokenResponse(newAccessToken,newRefreshToken);

    }

}
