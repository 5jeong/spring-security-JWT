package com.example.springjwt.oauth2.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response{
    private final Map<String,Object> attribute;

    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public SocialProvider getProvider() {
        return SocialProvider.KAKAO;
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        Map<String,Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
        return kakaoAccount.get("email").toString();
    }

    @Override
    public String getName() {
        Map<String,Object> kakaoProperties = (Map<String, Object>) attribute.get("properties");
        return kakaoProperties.get("nickname").toString();
    }
}
