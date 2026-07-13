package Registro.Citas.Service;

import Registro.Citas.Client.EmpleadoClient;
import Registro.Citas.Client.MascotaClient;
import Registro.Citas.Client.PropietarioClient;
import Registro.Citas.Modelo.CitaMedica;
import Registro.Citas.Modelo.EmpleadosDTO;
import Registro.Citas.Modelo.MascotaDTO;
import Registro.Citas.Modelo.PropietarioDTO;
import Registro.Citas.Repository.CitaMedicaRepository;
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
class CitaMedicaServiceTest {

    //Repositorio simulado, no toca la base de datos real
    @Mock
    private CitaMedicaRepository citaMedicaRepository;

    //Los clientes Feign tambien se simulan, asi no se hacen llamadas HTTP reales a otros microservicios
    @Mock
    private PropietarioClient propietarioClient;

    @Mock
    private MascotaClient mascotaClient;

    @Mock
    private EmpleadoClient empleadoClient;

    //Instancia real de CitaMedicaService con los mocks de arriba inyectados adentro
    @InjectMocks
    private CitaMedicaService citaMedicaService;

    private CitaMedica cita;

    //Se ejecuta antes de cada @Test para que ningun test deje datos "sucios" para el siguiente
    @BeforeEach
    void setUp() {
        cita = new CitaMedica();
        cita.setCodigoConsulta("CITA-2026-001");
        cita.setFechaHora(LocalDateTime.now().plusDays(1));
        cita.setMotivo("Vacunacion anual");
        cita.setEstado("PROGRAMADA");
        cita.setObservaciones("Sin observaciones");
        cita.setCodigoMicrochip("985121012345");
        cita.setRunPropietario("12.345.678-9");
        cita.setRunEmpleado("98.765.432-1");
    }

    // ============ TESTS DE GUARDAR ============

    @Test
    void guardarCitaMedica_cuandoTodoEsValido_debeGuardarYRetornarCita() {
        //No existe otra cita con el mismo codigo
        when(citaMedicaRepository.findByCodigoConsulta("CITA-2026-001")).thenReturn(Optional.empty());
        //Los tres microservicios externos responden que si existen
        when(propietarioClient.obtenerPropietarioporRun("12.345.678-9")).thenReturn(new PropietarioDTO());
        when(mascotaClient.obtenerMascotaporCodigo("985121012345")).thenReturn(new MascotaDTO());
        when(empleadoClient.obtenerEmpleadoporRun("98.765.432-1")).thenReturn(new EmpleadosDTO());
        when(citaMedicaRepository.save(cita)).thenReturn(cita);

        Optional<CitaMedica> resultado = citaMedicaService.guardarCitaMedica(cita);

        assertTrue(resultado.isPresent());
        assertEquals("CITA-2026-001", resultado.get().getCodigoConsulta());
        verify(citaMedicaRepository, times(1)).save(cita);
    }

    @Test
    void guardarCitaMedica_cuandoCodigoYaExiste_debeLanzarRuntimeException() {
        when(citaMedicaRepository.findByCodigoConsulta("CITA-2026-001")).thenReturn(Optional.of(cita));

        assertThrows(RuntimeException.class, () -> citaMedicaService.guardarCitaMedica(cita));
        //No deberia haber consultado a ningun microservicio externo, la validacion corta antes
        verify(propietarioClient, never()).obtenerPropietarioporRun(anyString());
        verify(citaMedicaRepository, never()).save(any());
    }

    @Test
    void guardarCitaMedica_cuandoPropietarioNoExiste_debeLanzarRuntimeException() {
        when(citaMedicaRepository.findByCodigoConsulta("CITA-2026-001")).thenReturn(Optional.empty());
        when(propietarioClient.obtenerPropietarioporRun("12.345.678-9")).thenThrow(mock(FeignException.NotFound.class));

        assertThrows(RuntimeException.class, () -> citaMedicaService.guardarCitaMedica(cita));
        verify(citaMedicaRepository, never()).save(any());
    }

    @Test
    void guardarCitaMedica_cuandoMascotaNoExiste_debeLanzarRuntimeException() {
        when(citaMedicaRepository.findByCodigoConsulta("CITA-2026-001")).thenReturn(Optional.empty());
        when(propietarioClient.obtenerPropietarioporRun("12.345.678-9")).thenReturn(new PropietarioDTO());
        when(mascotaClient.obtenerMascotaporCodigo("985121012345")).thenThrow(mock(FeignException.NotFound.class));

        assertThrows(RuntimeException.class, () -> citaMedicaService.guardarCitaMedica(cita));
        verify(citaMedicaRepository, never()).save(any());
    }

    @Test
    void guardarCitaMedica_cuandoEmpleadoNoExiste_debeLanzarRuntimeException() {
        when(citaMedicaRepository.findByCodigoConsulta("CITA-2026-001")).thenReturn(Optional.empty());
        when(propietarioClient.obtenerPropietarioporRun("12.345.678-9")).thenReturn(new PropietarioDTO());
        when(mascotaClient.obtenerMascotaporCodigo("985121012345")).thenReturn(new MascotaDTO());
        when(empleadoClient.obtenerEmpleadoporRun("98.765.432-1")).thenThrow(mock(FeignException.NotFound.class));

        assertThrows(RuntimeException.class, () -> citaMedicaService.guardarCitaMedica(cita));
        verify(citaMedicaRepository, never()).save(any());
    }

    @Test
    void guardarCitaMedica_cuandoFallaLaBaseDeDatos_debeLanzarRuntimeException() {
        when(citaMedicaRepository.findByCodigoConsulta("CITA-2026-001")).thenReturn(Optional.empty());
        when(propietarioClient.obtenerPropietarioporRun("12.345.678-9")).thenReturn(new PropietarioDTO());
        when(mascotaClient.obtenerMascotaporCodigo("985121012345")).thenReturn(new MascotaDTO());
        when(empleadoClient.obtenerEmpleadoporRun("98.765.432-1")).thenReturn(new EmpleadosDTO());
        when(citaMedicaRepository.save(cita)).thenThrow(new DataAccessException("Error de conexion") {});

        assertThrows(RuntimeException.class, () -> citaMedicaService.guardarCitaMedica(cita));
    }

    // ============ TESTS DE BUSCAR ============

    @Test
    void buscarPorCodigoConsulta_cuandoExiste_debeRetornarCita() {
        when(citaMedicaRepository.findByCodigoConsulta("CITA-2026-001")).thenReturn(Optional.of(cita));

        Optional<CitaMedica> resultado = citaMedicaService.buscarPorCodigoConsulta("CITA-2026-001");

        assertTrue(resultado.isPresent());
        assertEquals("Vacunacion anual", resultado.get().getMotivo());
    }

    @Test
    void buscarPorCodigoConsulta_cuandoNoExiste_debeRetornarEmpty() {
        when(citaMedicaRepository.findByCodigoConsulta("NO-EXISTE")).thenReturn(Optional.empty());

        Optional<CitaMedica> resultado = citaMedicaService.buscarPorCodigoConsulta("NO-EXISTE");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorCodigoConsulta_cuandoFallaLaConsulta_debeLanzarRuntimeException() {
        when(citaMedicaRepository.findByCodigoConsulta(anyString())).thenThrow(new RuntimeException("fallo inesperado"));

        assertThrows(RuntimeException.class, () -> citaMedicaService.buscarPorCodigoConsulta("CITA-2026-001"));
    }

    // ============ TESTS DE ACTUALIZAR ============

    @Test
    void actualizarCitaMedica_cuandoExiste_debeRetornarCitaActualizada() {
        CitaMedica datoNuevo = new CitaMedica();
        datoNuevo.setEstado("ATENDIDA");
        datoNuevo.setObservaciones("Paciente estable");

        when(citaMedicaRepository.findByCodigoConsulta("CITA-2026-001")).thenReturn(Optional.of(cita));
        when(citaMedicaRepository.save(any(CitaMedica.class))).thenReturn(cita);

        CitaMedica resultado = citaMedicaService.actualizarCitaMedica("CITA-2026-001", datoNuevo);

        assertNotNull(resultado);
        assertEquals("ATENDIDA", resultado.getEstado());
        assertEquals("Paciente estable", resultado.getObservaciones());
        verify(citaMedicaRepository, times(1)).save(any(CitaMedica.class));
    }

    @Test
    void actualizarCitaMedica_cuandoNoExiste_debeRetornarNull() {
        CitaMedica datoNuevo = new CitaMedica();
        datoNuevo.setEstado("ATENDIDA");
        datoNuevo.setObservaciones("Paciente estable");

        when(citaMedicaRepository.findByCodigoConsulta("NO-EXISTE")).thenReturn(Optional.empty());

        CitaMedica resultado = citaMedicaService.actualizarCitaMedica("NO-EXISTE", datoNuevo);

        assertNull(resultado);
        verify(citaMedicaRepository, never()).save(any());
    }

    @Test
    void actualizarCitaMedica_cuandoEstadoEsVacio_debeLanzarIllegalArgumentException() {
        CitaMedica datoNuevo = new CitaMedica();
        datoNuevo.setEstado(" ");
        datoNuevo.setObservaciones("Paciente estable");

        assertThrows(IllegalArgumentException.class,
                () -> citaMedicaService.actualizarCitaMedica("CITA-2026-001", datoNuevo));
        verify(citaMedicaRepository, never()).findByCodigoConsulta(anyString());
    }

    @Test
    void actualizarCitaMedica_cuandoObservacionesEsVacia_debeLanzarIllegalArgumentException() {
        CitaMedica datoNuevo = new CitaMedica();
        datoNuevo.setEstado("ATENDIDA");
        datoNuevo.setObservaciones("");

        assertThrows(IllegalArgumentException.class,
                () -> citaMedicaService.actualizarCitaMedica("CITA-2026-001", datoNuevo));
        verify(citaMedicaRepository, never()).findByCodigoConsulta(anyString());
    }

    // ============ TESTS DE ELIMINAR ============

    @Test
    void eliminarCitaMedica_cuandoExiste_debeRetornarTrue() {
        when(citaMedicaRepository.findByCodigoConsulta("CITA-2026-001")).thenReturn(Optional.of(cita));
        doNothing().when(citaMedicaRepository).delete(cita);

        boolean resultado = citaMedicaService.eliminarCitaMedica("CITA-2026-001");

        assertTrue(resultado);
        verify(citaMedicaRepository, times(1)).delete(cita);
    }

    @Test
    void eliminarCitaMedica_cuandoNoExiste_debeRetornarFalse() {
        when(citaMedicaRepository.findByCodigoConsulta("NO-EXISTE")).thenReturn(Optional.empty());

        boolean resultado = citaMedicaService.eliminarCitaMedica("NO-EXISTE");

        assertFalse(resultado);
        verify(citaMedicaRepository, never()).delete(any());
    }

    // ============ TESTS DE OBTENER CITAS POR MICROCHIP ============

    @Test
    void obtenerCitasPorMicrochip_cuandoNoHayCitas_debeRetornarListaVacia() {
        when(citaMedicaRepository.findByCodigoMicrochip("985121012345")).thenReturn(List.of());

        List<?> resultado = citaMedicaService.obtenerCitasPorMicrochip("985121012345");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerCitasPorMicrochip_cuandoHayCitas_debeRetornarListaConDetalle() {
        when(citaMedicaRepository.findByCodigoMicrochip("985121012345")).thenReturn(List.of(cita));
        when(propietarioClient.obtenerPropietarioporRun("12.345.678-9")).thenReturn(new PropietarioDTO());
        when(mascotaClient.obtenerMascotaporCodigo("985121012345")).thenReturn(new MascotaDTO());
        when(empleadoClient.obtenerEmpleadoporRun("98.765.432-1")).thenReturn(new EmpleadosDTO());

        var resultado = citaMedicaService.obtenerCitasPorMicrochip("985121012345");

        assertEquals(1, resultado.size());
        assertEquals("CITA-2026-001", resultado.get(0).getCodigoConsulta());
    }
}
