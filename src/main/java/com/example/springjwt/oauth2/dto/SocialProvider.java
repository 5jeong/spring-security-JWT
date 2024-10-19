package com.example.springjwt.oauth2.dto;

public enum SocialProvider {
    KAKAO,
    GOOGLE;

    // 정적 팩토리 메서드: registrationId를 기반으로 SocialProvider 반환
    public static SocialProvider from(String registrationId) {
        switch (registrationId.toLowerCase()) {
            case "google":
                return GOOGLE;
            case "kakao":
                return KAKAO;
            default:
                throw new IllegalArgumentException("지원하지 않는 로그인 제공자: " + registrationId);
        }
    }
}
