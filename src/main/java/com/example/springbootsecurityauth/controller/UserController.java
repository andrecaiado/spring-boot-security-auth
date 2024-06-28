package com.example.springbootsecurityauth.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/user")
public class UserController {

    @PreAuthorize("hasRole('USERs')")
    @GetMapping("/me")
    public String user(Principal principal) {
        return "Hello, " + principal.getName() + "!";
    }
}
