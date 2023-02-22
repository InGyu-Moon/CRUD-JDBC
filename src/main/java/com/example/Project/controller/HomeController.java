package com.example.Project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {
    @GetMapping("/")
    public String hello(){
        return "login";
    }
    @GetMapping("/login")
    public String login(){
        return "login";
    }

}
