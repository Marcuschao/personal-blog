package com.blog.personalblogbackend.controller;

import com.blog.personalblogbackend.common.Result;
import com.blog.personalblogbackend.dto.LoginRequest;
import com.blog.personalblogbackend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        return Result.success(data);
    }
}
