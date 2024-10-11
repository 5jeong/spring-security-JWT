package com.example.springjwt.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        ObjectMapper mapper = new ObjectMapper();

        // 인증 실패시, 실패 메시지 응답
        response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401 인증실패
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);


        if(exception instanceof BadCredentialsException){
            mapper.writeValue(response.getWriter(),"Invalid username or password");
        }

    }
}
