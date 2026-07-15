package com.hospitalizacion.hospitalizacion.service;

import com.hospitalizacion.hospitalizacion.client.MascotaClient;
import com.hospitalizacion.hospitalizacion.model.Hospitalizado;
import com.hospitalizacion.hospitalizacion.model.HospitalizadoDTO;
import com.hospitalizacion.hospitalizacion.model.MascotaDTO;
import com.hospitalizacion.hospitalizacion.repository.HospitalizadoRepository;
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
class HospitalizadoServiceTest {

    //Repositorio simulado, no toca la base de datos real
    @Mock
    private HospitalizadoRepository hospitalizadoRepository;

    //Cliente Feign simulado, no hace llamadas HTTP reales al microservicio de Registro
    @Mock
    private MascotaClient mascotaClient;

    //Instancia real de HospitalizadoService con los mocks de arriba inyectados adentro
    @InjectMocks
    private HospitalizadoService hospitalizadoService;

    private Hospitalizado hospitalizado;

    //Se ejecuta antes de cada @Test para que ningun test deje datos "sucios" para el siguiente
    @BeforeEach
    void setUp() {
        hospitalizado = new Hospitalizado();
        hospitalizado.setCodigoHospitalizacion("HOSP-2024-001");
        hospitalizado.setSala("Sala UCI Veterinaria");
        hospitalizado.setHoraMonitoreo(LocalDateTime.now());
        hospitalizado.setDescripcion("Animal estable, en observacion post operatoria");
        hospitalizado.setCodigoMicrochip("985121012345");
    }

    // ============ TESTS DE GUARDAR ============

    @Test
    void guardarHospitalizacion_cuandoTodoEsValido_debeGuardarYRetornarHospitalizado() {
        when(hospitalizadoRepository.findByCodigoHospitalizacion("HOSP-2024-001")).thenReturn(Optional.empty());
        when(mascotaClient.obtenerMascotaporCodigo("985121012345")).thenReturn(new MascotaDTO());
        when(hospitalizadoRepository.save(hospitalizado)).thenReturn(hospitalizado);

        Optional<Hospitalizado> resultado = hospitalizadoService.guardarHospitalizacion(hospitalizado);

        assertTrue(resultado.isPresent());
        assertEquals("HOSP-2024-001", resultado.get().getCodigoHospitalizacion());
        verify(hospitalizadoRepository, times(1)).save(hospitalizado);
    }

    @Test
    void guardarHospitalizacion_cuandoCodigoYaExiste_debeLanzarRuntimeException() {
        when(hospitalizadoRepository.findByCodigoHospitalizacion("HOSP-2024-001")).thenReturn(Optional.of(hospitalizado));

        assertThrows(RuntimeException.class, () -> hospitalizadoService.guardarHospitalizacion(hospitalizado));
        verify(mascotaClient, never()).obtenerMascotaporCodigo(anyString());
        verify(hospitalizadoRepository, never()).save(any());
    }

    @Test
    void guardarHospitalizacion_cuandoMascotaNoExiste_debeLanzarRuntimeException() {
        when(hospitalizadoRepository.findByCodigoHospitalizacion("HOSP-2024-001")).thenReturn(Optional.empty());
        when(mascotaClient.obtenerMascotaporCodigo("985121012345")).thenThrow(mock(FeignException.NotFound.class));

        assertThrows(RuntimeException.class, () -> hospitalizadoService.guardarHospitalizacion(hospitalizado));
        verify(hospitalizadoRepository, never()).save(any());
    }

    @Test
    void guardarHospitalizacion_cuandoFallaLaBaseDeDatos_debeLanzarRuntimeException() {
        when(hospitalizadoRepository.findByCodigoHospitalizacion("HOSP-2024-001")).thenReturn(Optional.empty());
        when(mascotaClient.obtenerMascotaporCodigo("985121012345")).thenReturn(new MascotaDTO());
        when(hospitalizadoRepository.save(hospitalizado)).thenThrow(new DataAccessException("Error de conexion") {});

        assertThrows(RuntimeException.class, () -> hospitalizadoService.guardarHospitalizacion(hospitalizado));
    }

    // ============ TESTS DE BUSCAR ============

    @Test
    void buscarPorCodigoHospitalizacion_cuandoExiste_debeRetornarHospitalizado() {
        when(hospitalizadoRepository.findByCodigoHospitalizacion("HOSP-2024-001")).thenReturn(Optional.of(hospitalizado));

        Optional<Hospitalizado> resultado = hospitalizadoService.buscarPorCodigoHospitalizacion("HOSP-2024-001");

        assertTrue(resultado.isPresent());
        assertEquals("Sala UCI Veterinaria", resultado.get().getSala());
    }

    @Test
    void buscarPorCodigoHospitalizacion_cuandoNoExiste_debeRetornarEmpty() {
        when(hospitalizadoRepository.findByCodigoHospitalizacion("no-existe")).thenReturn(Optional.empty());

        Optional<Hospitalizado> resultado = hospitalizadoService.buscarPorCodigoHospitalizacion("no-existe");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorCodigoHospitalizacion_cuandoFallaLaConsulta_debeLanzarRuntimeException() {
        when(hospitalizadoRepository.findByCodigoHospitalizacion(anyString())).thenThrow(new RuntimeException("fallo inesperado"));

        assertThrows(RuntimeException.class, () -> hospitalizadoService.buscarPorCodigoHospitalizacion("HOSP-2024-001"));
    }

    // ============ TESTS DE DETALLE COMPLETO ============

    @Test
    void obtenerDetalleCompletoHospitalizacion_cuandoExiste_debeRetornarDetalleConMascota() {
        when(hospitalizadoRepository.findByCodigoHospitalizacion("HOSP-2024-001")).thenReturn(Optional.of(hospitalizado));
        when(mascotaClient.obtenerMascotaporCodigo("985121012345")).thenReturn(new MascotaDTO());

        Optional<HospitalizadoDTO> resultado = hospitalizadoService.obtenerDetalleCompletoHospitalizacion("HOSP-2024-001");

        assertTrue(resultado.isPresent());
        assertEquals("HOSP-2024-001", resultado.get().getCodigoHospitalizacion());
        assertNotNull(resultado.get().getMascotaDTO());
    }

    @Test
    void obtenerDetalleCompletoHospitalizacion_cuandoNoExiste_debeRetornarEmpty() {
        when(hospitalizadoRepository.findByCodigoHospitalizacion("no-existe")).thenReturn(Optional.empty());

        Optional<HospitalizadoDTO> resultado = hospitalizadoService.obtenerDetalleCompletoHospitalizacion("no-existe");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerDetalleCompletoHospitalizacion_cuandoMascotaNoDisponible_debeLanzarRuntimeException() {
        when(hospitalizadoRepository.findByCodigoHospitalizacion("HOSP-2024-001")).thenReturn(Optional.of(hospitalizado));
        when(mascotaClient.obtenerMascotaporCodigo("985121012345")).thenThrow(mock(FeignException.NotFound.class));

        assertThrows(RuntimeException.class, () -> hospitalizadoService.obtenerDetalleCompletoHospitalizacion("HOSP-2024-001"));
    }
}
