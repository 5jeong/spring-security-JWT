package com.example.springjwt.controller;

import com.example.springjwt.dto.UserContext;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class MainController {

    @GetMapping("")
    public String mainP(@AuthenticationPrincipal UserContext userDetails){
        System.out.println(userDetails);
        return "Main Controller" ;
    }
}
