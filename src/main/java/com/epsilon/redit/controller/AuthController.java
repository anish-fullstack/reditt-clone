package com.epsilon.redit.controller;

import com.epsilon.redit.dto.AuthenticationResponse;
import com.epsilon.redit.dto.LoginDto;
import com.epsilon.redit.dto.SignUpDto;
import com.epsilon.redit.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpDto signUpDto){
        authService.signUp(signUpDto);
        return new ResponseEntity<>("User registered successfully.", HttpStatus.OK);
    }

    @GetMapping("/accountVerification/{token}")
    public ResponseEntity<?> verifyAccount(@PathVariable("token") String token){
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account activated successfully", HttpStatus.OK);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginDto loginDto){
        return authService.login(loginDto);
    }
}
