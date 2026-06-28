package com.example.Identificacion.Controller;

import com.example.Identificacion.Dto.AuthResponse;
import com.example.Identificacion.Dto.LoginRequest;
import com.example.Identificacion.Dto.RegistroRequest;
import com.example.Identificacion.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<@Nullable Object> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
}

//http://localhost:8085/api/auth/login
//http://localhost:8085/api/auth/register
//http://localhost:8085/api/test