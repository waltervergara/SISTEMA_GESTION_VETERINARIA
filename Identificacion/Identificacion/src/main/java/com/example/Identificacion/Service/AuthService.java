package com.example.Identificacion.Service;

import com.example.Identificacion.Client.EmpleadoClient;
import com.example.Identificacion.Dto.AuthResponse;
import com.example.Identificacion.Dto.EmpleadoResponse;
import com.example.Identificacion.Dto.LoginRequest;
import com.example.Identificacion.Dto.RegistroRequest;
import com.example.Identificacion.Model.Rol;
import com.example.Identificacion.Model.Usuario;
import com.example.Identificacion.Repository.RolRepository;
import com.example.Identificacion.Repository.UsuarioRepository;
import com.example.Identificacion.Security.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional // Asegura la atomicidad de la operación en la BD
    public AuthResponse register(RegistroRequest request) {
        
        // 1. Validaciones preventivas locales
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya está en uso.");
        }
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El correo electrónico ya está registrado.");
        }

        // 2. Mapeo y asignación de roles
        Set<Rol> roles = new HashSet<>();
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            Rol defaultRol = rolRepository.findByName("ROLE_OWNER")
                    .orElseThrow(() -> new RuntimeException("Error: Rol por defecto no encontrado."));
            roles.add(defaultRol);
        } else {
            for (String r : request.getRoles()) {
                Rol rol = rolRepository.findByName(r)
                        .orElseThrow(() -> new RuntimeException("Error: El rol " + r + " no existe."));
                roles.add(rol);
            }
        }

        // Determinar si el usuario intenta registrarse como un empleado
        boolean esEmpleado = roles.stream()
                .anyMatch(r -> r.getName().equals("ROLE_VET") || r.getName().equals("ROLE_ASSISTANT"));

        // 3. EVALUACIÓN CON EL MICROSERVICIO DE EMPLEADOS (Basada en Email)
        if (esEmpleado) {
            EmpleadoResponse empleado;
            try {
                // Solicitamos los datos al microservicio de empleados usando el email del formulario
                empleado = empleadoClient.obtenerEmpleadoPorCorreo(request.getEmail());
            } catch (Exception e) {
                // Si el MS de empleados responde con un error o no está disponible
                throw new RuntimeException("No se pudo verificar el empleado. El sistema de Recursos Humanos no responde o el correo no pertenece a un empleado.");
            }

            if (empleado == null) {
                throw new RuntimeException("Evaluación fallida: No existe ningún empleado contratado con este correo electrónico.");
            }

            // EVALUACIÓN DE CORRESPONDENCIA ADICIONAL (Opcional, por si los campos del JSON varían de nombre)
            if (!empleado.getGmail().equalsIgnoreCase(request.getEmail())) {
                throw new RuntimeException("Evaluación fallida: El correo electrónico de registro no coincide con el registro oficial.");
            }
            
            System.out.println(">>> Evaluación exitosa para el empleado verificado: " + empleado.getNombre());
        }

        // 4. Si la evaluación pasa con éxito, procedemos a guardar el usuario en vet_identity_db
        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setEmail(request.getEmail());
        usuario.setRoles(roles);
        usuario.setIsActive(true);

        usuarioRepository.save(usuario);

        // 5. Generar Token y respuesta
        String token = jwtUtil.generateToken(usuario);
        Set<String> rolesStr = usuario.getRoles().stream().map(Rol::getName).collect(Collectors.toSet());

        return new AuthResponse(token, usuario.getId(), usuario.getUsername(), rolesStr);
    }

    public @Nullable Object login(LoginRequest request) {
        // Dejamos esto listo para cuando revisemos el flujo de inicio de sesión
        throw new UnsupportedOperationException("Unimplemented method 'login'");
    }
}