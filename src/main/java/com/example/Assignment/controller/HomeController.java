package com.example.Assignment.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomeController {
    @RequestMapping("/user")
    public String str(){
        return "Hello this is my backend assignmnt project";
    }
}
