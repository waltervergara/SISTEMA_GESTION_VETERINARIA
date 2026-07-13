package com.registro.laboratorio.service;

import com.registro.laboratorio.client.MascotaClient;
import com.registro.laboratorio.model.LabOrden;
import com.registro.laboratorio.model.LabOrdenDTO;
import com.registro.laboratorio.model.MascotaDTO;
import com.registro.laboratorio.repository.LabOrdenRepository;
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
class LabOrdenServiceTest {

    //Repositorio simulado, no toca la base de datos real
    @Mock
    private LabOrdenRepository labOrdenRepository;

    //Cliente Feign simulado, no hace llamadas HTTP reales al microservicio de Registro
    @Mock
    private MascotaClient mascotaClient;

    //Instancia real de LabOrdenService con los mocks de arriba inyectados adentro
    @InjectMocks
    private LabOrdenService labOrdenService;

    private LabOrden labOrden;

    //Se ejecuta antes de cada @Test para que ningun test deje datos "sucios" para el siguiente
    @BeforeEach
    void setUp() {
        labOrden = new LabOrden();
        labOrden.setNombreOrden("LAB-2024-001");
        labOrden.setFechaPedido(LocalDateTime.now());
        labOrden.setTipoExamen("Hemograma completo");
        labOrden.setEstado("Pendiente");
        labOrden.setDescripcion("Examen de sangre para control rutinario");
        labOrden.setCodigoMicrochip("985121012345");
    }

    // ============ TESTS DE GUARDAR ============

    @Test
    void guardarLabOrden_cuandoTodoEsValido_debeGuardarYRetornarLabOrden() {
        when(labOrdenRepository.findByNombreOrden("LAB-2024-001")).thenReturn(Optional.empty());
        when(mascotaClient.obtenerMascotaporCodigo("985121012345")).thenReturn(new MascotaDTO());
        when(labOrdenRepository.save(labOrden)).thenReturn(labOrden);

        Optional<LabOrden> resultado = labOrdenService.guardarLabOrden(labOrden);

        assertTrue(resultado.isPresent());
        assertEquals("LAB-2024-001", resultado.get().getNombreOrden());
        verify(labOrdenRepository, times(1)).save(labOrden);
    }

    @Test
    void guardarLabOrden_cuandoNombreYaExiste_debeLanzarRuntimeException() {
        when(labOrdenRepository.findByNombreOrden("LAB-2024-001")).thenReturn(Optional.of(labOrden));

        assertThrows(RuntimeException.class, () -> labOrdenService.guardarLabOrden(labOrden));
        verify(mascotaClient, never()).obtenerMascotaporCodigo(anyString());
        verify(labOrdenRepository, never()).save(any());
    }

    @Test
    void guardarLabOrden_cuandoMascotaNoExiste_debeLanzarRuntimeException() {
        when(labOrdenRepository.findByNombreOrden("LAB-2024-001")).thenReturn(Optional.empty());
        when(mascotaClient.obtenerMascotaporCodigo("985121012345")).thenThrow(mock(FeignException.NotFound.class));

        assertThrows(RuntimeException.class, () -> labOrdenService.guardarLabOrden(labOrden));
        verify(labOrdenRepository, never()).save(any());
    }

    @Test
    void guardarLabOrden_cuandoFallaLaBaseDeDatos_debeLanzarRuntimeException() {
        when(labOrdenRepository.findByNombreOrden("LAB-2024-001")).thenReturn(Optional.empty());
        when(mascotaClient.obtenerMascotaporCodigo("985121012345")).thenReturn(new MascotaDTO());
        when(labOrdenRepository.save(labOrden)).thenThrow(new DataAccessException("Error de conexion") {});

        assertThrows(RuntimeException.class, () -> labOrdenService.guardarLabOrden(labOrden));
    }

    // ============ TESTS DE BUSCAR ============

    @Test
    void buscarPorNombreLabOrden_cuandoExiste_debeRetornarLabOrden() {
        when(labOrdenRepository.findByNombreOrden("LAB-2024-001")).thenReturn(Optional.of(labOrden));

        Optional<LabOrden> resultado = labOrdenService.buscarPorNombreLabOrden("LAB-2024-001");

        assertTrue(resultado.isPresent());
        assertEquals("Hemograma completo", resultado.get().getTipoExamen());
    }

    @Test
    void buscarPorNombreLabOrden_cuandoNoExiste_debeRetornarEmpty() {
        when(labOrdenRepository.findByNombreOrden("no-existe")).thenReturn(Optional.empty());

        Optional<LabOrden> resultado = labOrdenService.buscarPorNombreLabOrden("no-existe");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorNombreLabOrden_cuandoFallaLaConsulta_debeLanzarRuntimeException() {
        when(labOrdenRepository.findByNombreOrden(anyString())).thenThrow(new RuntimeException("fallo inesperado"));

        assertThrows(RuntimeException.class, () -> labOrdenService.buscarPorNombreLabOrden("LAB-2024-001"));
    }

    // ============ TESTS DE DETALLE COMPLETO ============

    @Test
    void obtenerDetalleCompletoLabOrden_cuandoExiste_debeRetornarDetalleConMascota() {
        when(labOrdenRepository.findByNombreOrden("LAB-2024-001")).thenReturn(Optional.of(labOrden));
        when(mascotaClient.obtenerMascotaporCodigo("985121012345")).thenReturn(new MascotaDTO());

        Optional<LabOrdenDTO> resultado = labOrdenService.obtenerDetalleCompletoLabOrden("LAB-2024-001");

        assertTrue(resultado.isPresent());
        assertEquals("LAB-2024-001", resultado.get().getNombre());
        assertNotNull(resultado.get().getMascotaDTO());
    }

    @Test
    void obtenerDetalleCompletoLabOrden_cuandoNoExiste_debeRetornarEmpty() {
        when(labOrdenRepository.findByNombreOrden("no-existe")).thenReturn(Optional.empty());

        Optional<LabOrdenDTO> resultado = labOrdenService.obtenerDetalleCompletoLabOrden("no-existe");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerDetalleCompletoLabOrden_cuandoMascotaNoDisponible_debeLanzarRuntimeException() {
        when(labOrdenRepository.findByNombreOrden("LAB-2024-001")).thenReturn(Optional.of(labOrden));
        when(mascotaClient.obtenerMascotaporCodigo("985121012345")).thenThrow(mock(FeignException.NotFound.class));

        assertThrows(RuntimeException.class, () -> labOrdenService.obtenerDetalleCompletoLabOrden("LAB-2024-001"));
    }

    // ============ TESTS DE ACTUALIZAR ============

    @Test
    void actualizarLabOrde_cuandoExisteYEstadoValido_debeRetornarLabOrdenActualizada() {
        LabOrden datoNuevo = new LabOrden();
        datoNuevo.setEstado("Completado");

        when(labOrdenRepository.findByNombreOrden("LAB-2024-001")).thenReturn(Optional.of(labOrden));
        when(labOrdenRepository.save(any(LabOrden.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LabOrden resultado = labOrdenService.ActualizarLabOrde("LAB-2024-001", datoNuevo);

        assertNotNull(resultado);
        assertEquals("Completado", resultado.getEstado());
    }

    @Test
    void actualizarLabOrde_cuandoNoExiste_debeRetornarNull() {
        LabOrden datoNuevo = new LabOrden();
        datoNuevo.setEstado("Completado");

        when(labOrdenRepository.findByNombreOrden("no-existe")).thenReturn(Optional.empty());

        LabOrden resultado = labOrdenService.ActualizarLabOrde("no-existe", datoNuevo);

        assertNull(resultado);
        verify(labOrdenRepository, never()).save(any());
    }

    @Test
    void actualizarLabOrde_cuandoEstadoEsVacio_debeLanzarIllegalArgumentException() {
        LabOrden datoNuevo = new LabOrden();
        datoNuevo.setEstado(" ");

        assertThrows(IllegalArgumentException.class,
                () -> labOrdenService.ActualizarLabOrde("LAB-2024-001", datoNuevo));
        verify(labOrdenRepository, never()).save(any());
    }
}
