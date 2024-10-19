package com.example.springjwt.oauth2.service;

import com.example.springjwt.dto.UserContext;
import com.example.springjwt.entity.UserEntity;
import com.example.springjwt.oauth2.dto.GoogleResponse;
import com.example.springjwt.oauth2.dto.KakaoResponse;
import com.example.springjwt.oauth2.dto.OAuth2Response;
import com.example.springjwt.oauth2.dto.SocialProvider;
import com.example.springjwt.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        System.out.println("oAuth2User = " + oAuth2User);

        // 소셜 제공자 구분
        SocialProvider provider = SocialProvider.from(userRequest.getClientRegistration().getRegistrationId());

        // 소셜 제공자별 응답 객체 생성
        OAuth2Response oAuth2Response = switch (provider){
            case KAKAO -> new KakaoResponse(attributes);
            case GOOGLE -> new GoogleResponse(attributes);
        };

        UserEntity user = getUserInfo(oAuth2Response);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole()));

        return UserContext.fromSocialLogin(user,authorities,attributes);

    }

    private UserEntity getUserInfo(OAuth2Response oAuth2Response) {
        UserEntity user = userRepository.findByEmail(oAuth2Response.getEmail()).orElse(null);

        // 만약 사용자가 존재하지 않으면 신규 사용자 생성
        if (user == null) {
            user = registerNewUser(oAuth2Response);
        } else{
            // 기존 사용자라면 소셜 계정 연동 정보만 업데이트
            user.linkSocialAccount(oAuth2Response.getProvider());
            userRepository.save(user);
        }
        return user;
    }

    private UserEntity registerNewUser(OAuth2Response oAuth2Response) {
        UserEntity newUser = new UserEntity();
        newUser.setUsername(oAuth2Response.getName());
        newUser.setEmail(oAuth2Response.getEmail());
        newUser.setRole("ROLE_USER");

        // 소셜 계정 연동 정보 설정
        newUser.linkSocialAccount(oAuth2Response.getProvider());
        userRepository.save(newUser);

        return newUser;
    }
}
