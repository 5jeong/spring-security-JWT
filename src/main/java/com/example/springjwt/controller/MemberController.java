package com.example.springjwt.controller;

import com.example.springjwt.dto.JoinDto;
import com.example.springjwt.dto.TokenResponse;
import com.example.springjwt.jwt.JWTUtil;
import com.example.springjwt.jwt.RefreshTokenService;
import com.example.springjwt.jwt.exception.TokenException;
import com.example.springjwt.service.JoinService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final JoinService joinService;

    @PostMapping("/join")
    public ResponseEntity<String> joinProcess(@RequestBody JoinDto joinDto) {
        String result = joinService.joinProcess(joinDto);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
