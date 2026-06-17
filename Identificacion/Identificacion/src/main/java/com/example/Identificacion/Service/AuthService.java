package com.example.Identificacion.Service;

import com.example.Identificacion.Client.EmpleadoClient;
import com.example.Identificacion.Client.PropietarioClient;
import com.example.Identificacion.Dto.AuthResponse;
import com.example.Identificacion.Dto.EmpleadoRequest;
import com.example.Identificacion.Dto.LoginRequest;
import com.example.Identificacion.Dto.PropietarioRequest;
import com.example.Identificacion.Dto.RegistroRequest;
import com.example.Identificacion.Model.Rol;
import com.example.Identificacion.Model.Usuario;
import com.example.Identificacion.Repository.RolRepository;
import com.example.Identificacion.Repository.UsuarioRepository;
import com.example.Identificacion.Security.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmpleadoClient empleadoClient;
    private final PropietarioClient propietarioClient; // <--- NUEVO CLIENTE

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario o contraseña incorrectos"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Usuario o contraseña incorrectos");
        }

        String token = jwtUtil.generateToken(usuario);
        Set<String> roles = usuario.getRoles().stream().map(Rol::getName).collect(Collectors.toSet());

        return new AuthResponse(token, usuario.getId(), usuario.getUsername(), roles);
    }

    public AuthResponse register(RegistroRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setEmail(request.getEmail());
        usuario.setIsActive(true);

        Set<Rol> roles = new HashSet<>();
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            Rol rolOwner = rolRepository.findByName("ROLE_OWNER")
                    .orElseThrow(() -> new RuntimeException("Rol ROLE_OWNER no encontrado"));
            roles.add(rolOwner);
        } else {
            for (String rolName : request.getRoles()) {
                Rol rol = rolRepository.findByName(rolName)
                        .orElseThrow(() -> new RuntimeException("Rol " + rolName + " no encontrado"));
                roles.add(rol);
            }
        }
        usuario.setRoles(roles);
        usuarioRepository.save(usuario);

        // --- LÓGICA DE COMUNICACIÓN ---
        boolean esEmpleado = roles.stream()
                .anyMatch(rol -> rol.getName().equals("ROLE_VET") || rol.getName().equals("ROLE_ASSISTANT"));
        
        boolean esDueno = roles.stream()
                .anyMatch(rol -> rol.getName().equals("ROLE_OWNER"));

        if (esEmpleado) {
            try {
                String cargo = roles.stream()
                        .filter(rol -> rol.getName().equals("ROLE_VET"))
                        .findFirst()
                        .map(rol -> "Veterinario")
                        .orElse("Asistente");

                EmpleadoRequest empleadoRequest = new EmpleadoRequest(
                        "TEMP-" + usuario.getId(), usuario.getUsername(), LocalDate.of(2000, 1, 1),
                        cargo, usuario.getEmail(), "000000000"
                );
                empleadoClient.crearEmpleado(empleadoRequest);
            } catch (Exception e) {
                System.out.println(">>> ERROR MS Empleados: " + e.getMessage());
            }
        }

        // NUEVA LÓGICA PARA PROPIETARIOS
        if (esDueno) {
            try {
                PropietarioRequest propietarioRequest = new PropietarioRequest(
                        "TEMP-" + usuario.getId(),   // runPropietario temporal
                        usuario.getUsername(),       // nombre (usamos el username)
                        "Pendiente",                 // apellido temporal (porque no lo pedimos en el registro)
                        usuario.getEmail(),          // correo
                        "000000000"                  // telefono temporal
                );
                propietarioClient.crearPropietario(propietarioRequest);
            } catch (Exception e) {
                System.out.println(">>> ERROR MS Propietarios: " + e.getMessage());
            }
        }
        // ------------------------------------

        String token = jwtUtil.generateToken(usuario);
        Set<String> rolesStr = usuario.getRoles().stream().map(Rol::getName).collect(Collectors.toSet());

        return new AuthResponse(token, usuario.getId(), usuario.getUsername(), rolesStr);
    }
}