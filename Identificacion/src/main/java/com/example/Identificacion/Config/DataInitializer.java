package com.example.Identificacion.Config;

import com.example.Identificacion.Model.Rol;
import com.example.Identificacion.Model.Usuario;
import com.example.Identificacion.Repository.RolRepository;
import com.example.Identificacion.Repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer {

    @Bean
    CommandLineRunner init(UsuarioRepository usuarioRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Nos aseguramos de que los roles existan (Flyway ya los creó, pero es por seguridad)
            if (rolRepository.findByName("ROLE_VET").isEmpty()) {
                Rol vetRole = new Rol();
                vetRole.setName("ROLE_VET");
                rolRepository.save(vetRole);
            }

            // 2. Creamos el usuario dr_garcia SOLO si no existe
            if (usuarioRepository.findByUsername("dr_garcia").isEmpty()) {
                Usuario user = new Usuario();
                user.setUsername("dr_garcia");
                // ¡AQUÍ ESTÁ LA MAGIA! Encriptamos la contraseña real usando Spring
                user.setPassword(passwordEncoder.encode("123456")); 
                user.setEmail("garcia@vet.com");
                user.setIsActive(true);
                
                // Buscamos el rol de Veterinario
                Rol vetRole = rolRepository.findByName("ROLE_VET").get();
                user.setRoles(Set.of(vetRole));
                
                usuarioRepository.save(user);
                System.out.println(">>> Usuario dr_garcia creado con contraseña 123456");
            }
        };
    }
}