package com.example.springjwt.service;


import com.example.springjwt.dto.JoinDto;
import com.example.springjwt.entity.UserEntity;
import com.example.springjwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public String joinProcess(JoinDto joinDto) {
        String username = joinDto.getUsername();
        String password = joinDto.getPassword();

        Boolean isExist = userRepository.existsByUsername(username);
        if (isExist) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
        UserEntity data = new UserEntity();

        data.setUsername(username);
        data.setPassword(passwordEncoder.encode(password));
        data.setRole("ROLE_ADMIN");
        userRepository.save(data);
        return "회원가입이 완료되었습니다.";
    }
}
