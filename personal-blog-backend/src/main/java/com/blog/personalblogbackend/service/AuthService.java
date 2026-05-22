package com.blog.personalblogbackend.service;

import com.blog.personalblogbackend.model.dto.auth.LoginResult;

public interface AuthService {

    LoginResult login(String username, String password, boolean rememberMe, String clientIp);
}
