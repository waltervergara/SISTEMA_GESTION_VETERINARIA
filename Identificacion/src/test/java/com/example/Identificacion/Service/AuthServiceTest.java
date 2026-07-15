package com.example.Identificacion.Service;

import com.example.Identificacion.Client.EmpleadoClient;
import com.example.Identificacion.Client.PropietarioClient;
import com.example.Identificacion.Dto.AuthResponse;
import com.example.Identificacion.Dto.LoginRequest;
import com.example.Identificacion.Dto.RegistroRequest;
import com.example.Identificacion.Model.Rol;
import com.example.Identificacion.Model.Usuario;
import com.example.Identificacion.Repository.RolRepository;
import com.example.Identificacion.Repository.UsuarioRepository;
import com.example.Identificacion.Security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*; //assertTrue, assertEquals, assertNull, etc.
import static org.mockito.Mockito.*; //when(), verify(), any(), never(), times(), doThrow()

//Activa Mockito en esta clase, sin esto @Mock y @InjectMocks no funcionan
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    //Repositorios y colaboradores simulados, no tocan la base de datos real ni firman JWT de verdad
    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private EmpleadoClient empleadoClient;

    @Mock
    private PropietarioClient propietarioClient;

    //Instancia real de AuthService con los mocks de arriba inyectados adentro
    @InjectMocks
    private AuthService authService;

    private Usuario usuario;
    private Rol rolOwner;

    //Se ejecuta antes de cada @Test para que ningun test deje datos "sucios" para el siguiente
    @BeforeEach
    void setUp() {
        rolOwner = new Rol(1L, "ROLE_OWNER");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("walter");
        usuario.setPassword("hash-encriptado");
        usuario.setEmail("walter@gmail.com");
        usuario.setIsActive(true);
        usuario.setRoles(new HashSet<>(Set.of(rolOwner)));
    }

    // ============ TESTS DE LOGIN ============

    @Test
    void login_cuandoCredencialesSonCorrectas_debeRetornarAuthResponseConToken() {
        LoginRequest request = new LoginRequest();
        request.setUsername("walter");
        request.setPassword("1234");

        when(usuarioRepository.findByUsername("walter")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("1234", "hash-encriptado")).thenReturn(true);
        when(jwtUtil.generateToken(usuario)).thenReturn("token-jwt-falso");

        AuthResponse resultado = authService.login(request);

        assertEquals("token-jwt-falso", resultado.getToken());
        assertEquals(1L, resultado.getUserId());
        assertEquals("walter", resultado.getUsername());
        assertTrue(resultado.getRoles().contains("ROLE_OWNER"));
    }

    @Test
    void login_cuandoUsuarioNoExiste_debeLanzarRuntimeException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("no-existe");
        request.setPassword("1234");

        when(usuarioRepository.findByUsername("no-existe")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(request));
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void login_cuandoPasswordEsIncorrecta_debeLanzarRuntimeException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("walter");
        request.setPassword("incorrecta");

        when(usuarioRepository.findByUsername("walter")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("incorrecta", "hash-encriptado")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.login(request));
        verify(jwtUtil, never()).generateToken(any());
    }

    // ============ TESTS DE REGISTER ============

    @Test
    void register_cuandoUsernameYaExiste_debeLanzarRuntimeException() {
        RegistroRequest request = new RegistroRequest();
        request.setUsername("walter");
        request.setPassword("1234");
        request.setEmail("nuevo@gmail.com");

        when(usuarioRepository.existsByUsername("walter")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(request));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void register_cuandoEmailYaExiste_debeLanzarRuntimeException() {
        RegistroRequest request = new RegistroRequest();
        request.setUsername("nuevo");
        request.setPassword("1234");
        request.setEmail("walter@gmail.com");

        when(usuarioRepository.existsByUsername("nuevo")).thenReturn(false);
        when(usuarioRepository.existsByEmail("walter@gmail.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(request));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void register_sinRoles_debeAsignarRoleOwnerYCrearPropietario() {
        RegistroRequest request = new RegistroRequest();
        request.setUsername("nuevo");
        request.setPassword("1234");
        request.setEmail("nuevo@gmail.com");

        when(usuarioRepository.existsByUsername("nuevo")).thenReturn(false);
        when(usuarioRepository.existsByEmail("nuevo@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("1234")).thenReturn("1234-encriptada");
        when(rolRepository.findByName("ROLE_OWNER")).thenReturn(Optional.of(rolOwner));
        when(jwtUtil.generateToken(any(Usuario.class))).thenReturn("token-jwt-falso");

        AuthResponse resultado = authService.register(request);

        assertEquals("token-jwt-falso", resultado.getToken());
        assertTrue(resultado.getRoles().contains("ROLE_OWNER"));
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        //Como el unico rol es ROLE_OWNER, debe crear al propietario en el otro microservicio, pero no al empleado
        verify(propietarioClient, times(1)).crearPropietario(any());
        verify(empleadoClient, never()).crearEmpleado(any());
    }

    @Test
    void register_cuandoRoleOwnerPorDefectoNoExiste_debeLanzarRuntimeException() {
        RegistroRequest request = new RegistroRequest();
        request.setUsername("nuevo");
        request.setPassword("1234");
        request.setEmail("nuevo@gmail.com");

        when(usuarioRepository.existsByUsername("nuevo")).thenReturn(false);
        when(usuarioRepository.existsByEmail("nuevo@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("1234")).thenReturn("1234-encriptada");
        when(rolRepository.findByName("ROLE_OWNER")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.register(request));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void register_conRolVet_debeCrearEmpleadoYNoPropietario() {
        RegistroRequest request = new RegistroRequest();
        request.setUsername("veterinario1");
        request.setPassword("1234");
        request.setEmail("vet@gmail.com");
        request.setRoles(Set.of("ROLE_VET"));

        Rol rolVet = new Rol(2L, "ROLE_VET");

        when(usuarioRepository.existsByUsername("veterinario1")).thenReturn(false);
        when(usuarioRepository.existsByEmail("vet@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("1234")).thenReturn("1234-encriptada");
        when(rolRepository.findByName("ROLE_VET")).thenReturn(Optional.of(rolVet));
        when(jwtUtil.generateToken(any(Usuario.class))).thenReturn("token-jwt-falso");

        AuthResponse resultado = authService.register(request);

        assertTrue(resultado.getRoles().contains("ROLE_VET"));
        verify(empleadoClient, times(1)).crearEmpleado(any());
        verify(propietarioClient, never()).crearPropietario(any());
    }

    @Test
    void register_cuandoRolSolicitadoNoExiste_debeLanzarRuntimeException() {
        RegistroRequest request = new RegistroRequest();
        request.setUsername("nuevo");
        request.setPassword("1234");
        request.setEmail("nuevo@gmail.com");
        request.setRoles(Set.of("ROLE_INEXISTENTE"));

        when(usuarioRepository.existsByUsername("nuevo")).thenReturn(false);
        when(usuarioRepository.existsByEmail("nuevo@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("1234")).thenReturn("1234-encriptada");
        when(rolRepository.findByName("ROLE_INEXISTENTE")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.register(request));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void register_cuandoFallaLaComunicacionConOtroMicroservicio_debeSeguirRegistrandoAlUsuario() {
        RegistroRequest request = new RegistroRequest();
        request.setUsername("nuevo");
        request.setPassword("1234");
        request.setEmail("nuevo@gmail.com");

        when(usuarioRepository.existsByUsername("nuevo")).thenReturn(false);
        when(usuarioRepository.existsByEmail("nuevo@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("1234")).thenReturn("1234-encriptada");
        when(rolRepository.findByName("ROLE_OWNER")).thenReturn(Optional.of(rolOwner));
        when(jwtUtil.generateToken(any(Usuario.class))).thenReturn("token-jwt-falso");
        //Simulamos que el microservicio de propietarios esta caido
        doThrow(new RuntimeException("servicio caido")).when(propietarioClient).crearPropietario(any());

        //El registro no deberia fallar aunque el otro microservicio no responda, el error solo se loguea
        AuthResponse resultado = authService.register(request);

        assertEquals("token-jwt-falso", resultado.getToken());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }
}
