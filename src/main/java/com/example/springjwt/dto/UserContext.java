package com.example.springjwt.dto;

import com.example.springjwt.entity.UserEntity;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class UserContext implements UserDetails, OAuth2User {

    public final UserEntity userEntity;
    private final List<GrantedAuthority> authorities; // 회원 권한(시큐리티에서 사용)
    private final Map<String, Object> attributes; // 소셜 로그인에서 제공한 사용자 정보

    // 공통 생성자 (폼 로그인과 소셜 로그인 모두 대응)
    public UserContext(UserEntity userEntity, List<GrantedAuthority> authorities,
                       Map<String, Object> attributes) {
        this.userEntity = userEntity;
        this.authorities = authorities;
        this.attributes = attributes;  // null 가능 (폼 로그인일 경우)
    }

    // 폼 로그인을 위한 정적 팩토리 메서드
    public static UserContext fromFormLogin(UserEntity userEntity, List<GrantedAuthority> authorities) {
        return new UserContext(userEntity, authorities, null);
    }

    // 소셜 로그인을 위한 정적 팩토리 메서드
    public static UserContext fromSocialLogin(UserEntity userEntity, List<GrantedAuthority> authorities,
                                              Map<String, Object> attributes) {
        return new UserContext(userEntity, authorities, attributes);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // OAuth2User 메서드 (소셜 로그시 모든 정보들)
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // 소셜로그인에서 제공하는 정보
    @Override
    public String getName() {
        return userEntity.getUsername();
    }
}
