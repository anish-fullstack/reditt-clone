package com.epsilon.redit.service;

import com.epsilon.redit.dto.AuthenticationResponse;
import com.epsilon.redit.dto.LoginDto;
import com.epsilon.redit.dto.SignUpDto;

public interface AuthService {

    void signUp(SignUpDto signUpDto);

    void verifyAccount(String token);

    AuthenticationResponse login(LoginDto loginDto);
}
