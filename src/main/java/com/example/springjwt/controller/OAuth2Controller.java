package com.example.springjwt.controller;

import com.example.springjwt.oauth2.service.OAuth2HeaderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OAuth2Controller {

    private final OAuth2HeaderService oAuth2HeaderService;

    @PostMapping("/oauth2-jwt-header")
    public String oauth2JwtHeader(HttpServletRequest request, HttpServletResponse response) {
        return oAuth2HeaderService.oauth2JwtHeaderSet(request, response);
    }
}
