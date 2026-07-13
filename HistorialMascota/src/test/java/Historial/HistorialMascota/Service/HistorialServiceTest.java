package Historial.HistorialMascota.Service;

import Historial.HistorialMascota.Client.CitaClient;
import Historial.HistorialMascota.Client.MascotaClient;
import Historial.HistorialMascota.Modelo.CitaDTO;
import Historial.HistorialMascota.Modelo.Historial;
import Historial.HistorialMascota.Modelo.HistorialDTO;
import Historial.HistorialMascota.Modelo.MascotaDTO;
import Historial.HistorialMascota.Repository.HistorialRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*; //assertTrue, assertEquals, assertNull, etc.
import static org.mockito.Mockito.*; //when(), verify(), any(), never(), times(), mock()

//Activa Mockito en esta clase, sin esto @Mock y @InjectMocks no funcionan
@ExtendWith(MockitoExtension.class)
class HistorialServiceTest {

    //Repositorio simulado, no toca la base de datos real
    @Mock
    private HistorialRepository historialRepository;

    //Clientes Feign simulados, no hacen llamadas HTTP reales a otros microservicios
    @Mock
    private CitaClient citaClient;

    @Mock
    private MascotaClient mascotaClient;

    //Instancia real de HistorialService con los mocks de arriba inyectados adentro
    @InjectMocks
    private HistorialService historialService;

    private Historial historial;

    //Se ejecuta antes de cada @Test para que ningun test deje datos "sucios" para el siguiente
    @BeforeEach
    void setUp() {
        historial = new Historial();
        historial.setCodigoMicrochip("985121012345");
        historial.setFechaCreacionHistorial(LocalDateTime.now().minusDays(1));
        historial.setObservacionesGenerales("Mascota en buen estado general, vacunas al dia");
    }

    // ============ TESTS DE GUARDAR ============

    @Test
    void guardarHistorial_cuandoTodoEsValido_debeGuardarYRetornarHistorial() {
        when(mascotaClient.obtenerMascotaporCodigo("985121012345")).thenReturn(new MascotaDTO());
        when(historialRepository.findByCodigoMicrochip("985121012345")).thenReturn(Optional.empty());
        when(historialRepository.save(historial)).thenReturn(historial);

        Optional<Historial> resultado = historialService.guardarHistorial(historial);

        assertTrue(resultado.isPresent());
        assertEquals("985121012345", resultado.get().getCodigoMicrochip());
        verify(historialRepository, times(1)).save(historial);
    }

    @Test
    void guardarHistorial_cuandoMascotaNoExiste_debeLanzarRuntimeException() {
        when(mascotaClient.obtenerMascotaporCodigo("985121012345")).thenThrow(mock(FeignException.NotFound.class));

        assertThrows(RuntimeException.class, () -> historialService.guardarHistorial(historial));
        verify(historialRepository, never()).save(any());
    }

    @Test
    void guardarHistorial_cuandoFallaLaComunicacionConMascotas_debeLanzarRuntimeException() {
        when(mascotaClient.obtenerMascotaporCodigo("985121012345")).thenThrow(new RuntimeException("servicio caido"));

        assertThrows(RuntimeException.class, () -> historialService.guardarHistorial(historial));
        verify(historialRepository, never()).save(any());
    }

    @Test
    void guardarHistorial_cuandoYaExisteElHistorial_debeLanzarRuntimeException() {
        when(mascotaClient.obtenerMascotaporCodigo("985121012345")).thenReturn(new MascotaDTO());
        when(historialRepository.findByCodigoMicrochip("985121012345")).thenReturn(Optional.of(historial));

        assertThrows(RuntimeException.class, () -> historialService.guardarHistorial(historial));
        verify(historialRepository, never()).save(any());
    }

    @Test
    void guardarHistorial_cuandoFallaLaBaseDeDatos_debeLanzarRuntimeException() {
        when(mascotaClient.obtenerMascotaporCodigo("985121012345")).thenReturn(new MascotaDTO());
        when(historialRepository.findByCodigoMicrochip("985121012345")).thenReturn(Optional.empty());
        when(historialRepository.save(historial)).thenThrow(new DataAccessException("Error de conexion") {});

        assertThrows(RuntimeException.class, () -> historialService.guardarHistorial(historial));
    }

    // ============ TESTS DE OBTENER HISTORIAL COMPLETO ============

    @Test
    void obtenerHistorialCompleto_cuandoExiste_debeRetornarDtoConCitas() {
        CitaDTO citaDTO = new CitaDTO();
        citaDTO.setCodigoConsulta("CITA-2026-001");

        when(historialRepository.findByCodigoMicrochip("985121012345")).thenReturn(Optional.of(historial));
        when(citaClient.obtenerCitasPorMicrochip("985121012345")).thenReturn(List.of(citaDTO));

        Optional<HistorialDTO> resultado = historialService.obtenerHistorialCompleto("985121012345");

        assertTrue(resultado.isPresent());
        assertEquals("985121012345", resultado.get().getCodigoMicrochip());
        assertEquals(1, resultado.get().getCitas().size());
    }

    @Test
    void obtenerHistorialCompleto_cuandoNoExiste_debeRetornarEmpty() {
        when(historialRepository.findByCodigoMicrochip("NO-EXISTE")).thenReturn(Optional.empty());

        Optional<HistorialDTO> resultado = historialService.obtenerHistorialCompleto("NO-EXISTE");

        assertTrue(resultado.isEmpty());
        verify(citaClient, never()).obtenerCitasPorMicrochip(anyString());
    }
}
