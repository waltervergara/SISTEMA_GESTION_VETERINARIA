package com.Tratamiento.Tratamiento.Service;

import com.Tratamiento.Tratamiento.Client.MascotaClient;
import com.Tratamiento.Tratamiento.Model.MascotaDTO;
import com.Tratamiento.Tratamiento.Model.Tratamiento;
import com.Tratamiento.Tratamiento.Model.TratamientoDTO;
import com.Tratamiento.Tratamiento.Repository.TratamientoRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*; //assertTrue, assertEquals, assertNull, etc.
import static org.mockito.Mockito.*; //when(), verify(), any(), never(), times(), mock()

//Activa Mockito en esta clase, sin esto @Mock y @InjectMocks no funcionan
@ExtendWith(MockitoExtension.class)
class TratamientoServiceTest {

    //Repositorio simulado, no toca la base de datos real
    @Mock
    private TratamientoRepository tratamientoRepository;

    //Cliente Feign simulado, no hace llamadas HTTP reales al microservicio de Registro
    @Mock
    private MascotaClient mascotaClient;

    //Instancia real de TratamientoService con los mocks de arriba inyectados adentro
    @InjectMocks
    private TratamientoService tratamientoService;

    private Tratamiento tratamiento;

    //Se ejecuta antes de cada @Test para que ningun test deje datos "sucios" para el siguiente
    @BeforeEach
    void setUp() {
        tratamiento = new Tratamiento();
        tratamiento.setNombre("Tratamiento-001");
        tratamiento.setDiagnostico("Infeccion bacteriana leve");
        tratamiento.setMedicacion("Amoxicilina 500mg cada 8 horas");
        tratamiento.setObservacion("Reposo por 3 dias, evitar ejercicio");
        tratamiento.setFechaRevision(LocalDateTime.now());
        tratamiento.setCodigoMicrochip("985121012345");
    }

    // ============ TESTS DE GUARDAR ============

    @Test
    void guardarTratamiento_cuandoTodoEsValido_debeGuardarYRetornarTratamiento() {
        when(tratamientoRepository.findByNombre("Tratamiento-001")).thenReturn(Optional.empty());
        when(mascotaClient.obtenerMascotaporCodigo("985121012345")).thenReturn(new MascotaDTO());
        when(tratamientoRepository.save(tratamiento)).thenReturn(tratamiento);

        Optional<Tratamiento> resultado = tratamientoService.guardarTratamiento(tratamiento);

        assertTrue(resultado.isPresent());
        assertEquals("Tratamiento-001", resultado.get().getNombre());
        verify(tratamientoRepository, times(1)).save(tratamiento);
    }

    @Test
    void guardarTratamiento_cuandoNombreYaExiste_debeLanzarRuntimeException() {
        when(tratamientoRepository.findByNombre("Tratamiento-001")).thenReturn(Optional.of(tratamiento));

        assertThrows(RuntimeException.class, () -> tratamientoService.guardarTratamiento(tratamiento));
        verify(mascotaClient, never()).obtenerMascotaporCodigo(anyString());
        verify(tratamientoRepository, never()).save(any());
    }

    @Test
    void guardarTratamiento_cuandoMascotaNoExiste_debeLanzarRuntimeException() {
        when(tratamientoRepository.findByNombre("Tratamiento-001")).thenReturn(Optional.empty());
        when(mascotaClient.obtenerMascotaporCodigo("985121012345")).thenThrow(mock(FeignException.NotFound.class));

        assertThrows(RuntimeException.class, () -> tratamientoService.guardarTratamiento(tratamiento));
        verify(tratamientoRepository, never()).save(any());
    }

    @Test
    void guardarTratamiento_cuandoFallaLaBaseDeDatos_debeLanzarRuntimeException() {
        when(tratamientoRepository.findByNombre("Tratamiento-001")).thenReturn(Optional.empty());
        when(mascotaClient.obtenerMascotaporCodigo("985121012345")).thenReturn(new MascotaDTO());
        when(tratamientoRepository.save(tratamiento)).thenThrow(new DataAccessException("Error de conexion") {});

        assertThrows(RuntimeException.class, () -> tratamientoService.guardarTratamiento(tratamiento));
    }

    // ============ TESTS DE BUSCAR ============

    @Test
    void buscarPorNombre_cuandoExiste_debeRetornarTratamiento() {
        when(tratamientoRepository.findByNombre("Tratamiento-001")).thenReturn(Optional.of(tratamiento));

        Optional<Tratamiento> resultado = tratamientoService.buscarPorNombre("Tratamiento-001");

        assertTrue(resultado.isPresent());
        assertEquals("Infeccion bacteriana leve", resultado.get().getDiagnostico());
    }

    @Test
    void buscarPorNombre_cuandoNoExiste_debeRetornarEmpty() {
        when(tratamientoRepository.findByNombre("no-existe")).thenReturn(Optional.empty());

        Optional<Tratamiento> resultado = tratamientoService.buscarPorNombre("no-existe");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorNombre_cuandoFallaLaConsulta_debeLanzarRuntimeException() {
        when(tratamientoRepository.findByNombre(anyString())).thenThrow(new RuntimeException("fallo inesperado"));

        assertThrows(RuntimeException.class, () -> tratamientoService.buscarPorNombre("Tratamiento-001"));
    }

    // ============ TESTS DE DETALLE COMPLETO ============

    @Test
    void obtenerDetalleCompleto_cuandoExiste_debeRetornarDetalleConMascota() {
        when(tratamientoRepository.findByNombre("Tratamiento-001")).thenReturn(Optional.of(tratamiento));
        when(mascotaClient.obtenerMascotaporCodigo("985121012345")).thenReturn(new MascotaDTO());

        Optional<TratamientoDTO> resultado = tratamientoService.obtenerDetalleCompleto("Tratamiento-001");

        assertTrue(resultado.isPresent());
        assertEquals("Tratamiento-001", resultado.get().getNombre());
        assertNotNull(resultado.get().getMascota());
    }

    @Test
    void obtenerDetalleCompleto_cuandoNoExisteElTratamiento_debeRetornarEmpty() {
        when(tratamientoRepository.findByNombre("no-existe")).thenReturn(Optional.empty());

        Optional<TratamientoDTO> resultado = tratamientoService.obtenerDetalleCompleto("no-existe");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerDetalleCompleto_cuandoMascotaNoDisponible_debeLanzarRuntimeException() {
        when(tratamientoRepository.findByNombre("Tratamiento-001")).thenReturn(Optional.of(tratamiento));
        when(mascotaClient.obtenerMascotaporCodigo("985121012345")).thenThrow(mock(FeignException.NotFound.class));

        assertThrows(RuntimeException.class, () -> tratamientoService.obtenerDetalleCompleto("Tratamiento-001"));
    }

    // ============ TESTS DE ACTUALIZAR ============

    @Test
    void actualizarTratamiento_cuandoExisteYDatosValidos_debeRetornarTratamientoActualizado() {
        Tratamiento datoNuevo = new Tratamiento();
        datoNuevo.setDiagnostico("Recuperacion favorable");
        datoNuevo.setMedicacion("Suspender medicacion");
        datoNuevo.setObservacion("Alta medica");
        datoNuevo.setFechaRevision(LocalDateTime.now().plusDays(7));

        when(tratamientoRepository.findByNombre("Tratamiento-001")).thenReturn(Optional.of(tratamiento));
        when(tratamientoRepository.save(any(Tratamiento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Tratamiento resultado = tratamientoService.actualizarTratamiento("Tratamiento-001", datoNuevo);

        assertNotNull(resultado);
        assertEquals("Recuperacion favorable", resultado.getDiagnostico());
        assertEquals("Suspender medicacion", resultado.getMedicacion());
        assertEquals("Alta medica", resultado.getObservacion());
    }

    @Test
    void actualizarTratamiento_cuandoNoExiste_debeRetornarNull() {
        when(tratamientoRepository.findByNombre("no-existe")).thenReturn(Optional.empty());

        Tratamiento resultado = tratamientoService.actualizarTratamiento("no-existe", tratamiento);

        assertNull(resultado);
        verify(tratamientoRepository, never()).save(any());
    }

    // Los siguientes tests prueban las validaciones de campos obligatorios que hace el
    // service ANTES de tocar el repositorio (por eso no hace falta configurar ningun mock aqui)

    @Test
    void actualizarTratamiento_cuandoDiagnosticoEsVacio_debeLanzarIllegalArgumentException() {
        tratamiento.setDiagnostico(" ");

        assertThrows(IllegalArgumentException.class,
                () -> tratamientoService.actualizarTratamiento("Tratamiento-001", tratamiento));
    }

    @Test
    void actualizarTratamiento_cuandoFechaEsNula_debeLanzarIllegalArgumentException() {
        tratamiento.setFechaRevision(null);

        assertThrows(IllegalArgumentException.class,
                () -> tratamientoService.actualizarTratamiento("Tratamiento-001", tratamiento));
    }

    @Test
    void actualizarTratamiento_cuandoMedicacionEsVacia_debeLanzarIllegalArgumentException() {
        tratamiento.setMedicacion("");

        assertThrows(IllegalArgumentException.class,
                () -> tratamientoService.actualizarTratamiento("Tratamiento-001", tratamiento));
    }

    @Test
    void actualizarTratamiento_cuandoObservacionEsVacia_debeLanzarIllegalArgumentException() {
        tratamiento.setObservacion("");

        assertThrows(IllegalArgumentException.class,
                () -> tratamientoService.actualizarTratamiento("Tratamiento-001", tratamiento));
    }
}
