package com.example.springjwt.entity;

import com.example.springjwt.oauth2.dto.SocialProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;
    private String email;
    private String password;
    private String role;

    // 카카오 연동여부
    @Column(name = "kakao_linked")
    private Boolean kakaoLinked = false;
    // 구글 연동여부
    @Column(name = "google_linked")
    private Boolean googleLinked = false;

    // 소셜 제공자에 따라 연동 정보를 동적으로 설정
    public void linkSocialAccount(SocialProvider provider) {
        switch (provider) {
            case KAKAO:
                this.kakaoLinked = true;
                break;
            case GOOGLE:
                this.googleLinked = true;
                break;
            default:
                throw new IllegalArgumentException("지원되지 않는 소셜 제공자입니다.");
        }
    }

}
