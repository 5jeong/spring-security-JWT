package com.example.springjwt.config;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

// AuditorAware 인터페이스를 구현하여 현재 인증된 사용자의 정보를 가져오는 클래스
public class AuditorAwareImpl implements AuditorAware<String> {

    // 현재 감사(audit) 정보를 반환하는 메서드
    @Override
    public Optional<String> getCurrentAuditor() {
        // Spring Security의 컨텍스트에서 현재 인증된 사용자 정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        authentication.getPrincipal()
        // 사용자 아이디를 저장할 변수를 초기화
        String userId = "";

        // 인증 정보가 null이 아닌 경우 (즉, 사용자가 인증된 상태인 경우)
        if (authentication != null) {
            // 인증된 사용자의 이름(보통은 사용자 아이디)을 가져와 userId에 저장
            userId = authentication.getName();
        }

        // Optional로 현재 감사자의 이름을 반환, 만약 인증 정보가 없으면 빈 문자열이 반환됨
        return Optional.of(userId);
    }
}
