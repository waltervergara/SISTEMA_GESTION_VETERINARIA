package com.example.Identificacion.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;         // El JWT
    private Long userId;         // EL ID que necesitará el MS de Empleados
    private String username;
    private Set<String> roles;   // Los permisos (ROLE_VET, etc.)
}