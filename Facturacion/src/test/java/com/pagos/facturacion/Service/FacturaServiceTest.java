package com.pagos.facturacion.Service;

import com.pagos.facturacion.Client.PropietarioClient;
import com.pagos.facturacion.Model.Factura;
import com.pagos.facturacion.Model.FacturaDTO;
import com.pagos.facturacion.Model.PropietarioDTO;
import com.pagos.facturacion.Repository.FacturaRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*; //assertTrue, assertEquals, assertNull, etc.
import static org.mockito.Mockito.*; //when(), verify(), any(), never(), times(), mock()

//Activa Mockito en esta clase, sin esto @Mock y @InjectMocks no funcionan
@ExtendWith(MockitoExtension.class)
class FacturaServiceTest {

    //Repositorio simulado, no toca la base de datos real
    @Mock
    private FacturaRepository facturaRepository;

    //Cliente Feign simulado, no hace llamadas HTTP reales al microservicio de Registro
    @Mock
    private PropietarioClient propietarioClient;

    //Instancia real de FacturaService con los mocks de arriba inyectados adentro
    @InjectMocks
    private FacturaService facturaService;

    private Factura factura;

    //Se ejecuta antes de cada @Test para que ningun test deje datos "sucios" para el siguiente
    @BeforeEach
    void setUp() {
        factura = new Factura();
        factura.setCodigoFactura("FAC-2024-001");
        factura.setDetalles("Consulta veterinaria y vacunacion");
        factura.setFechaEmision(LocalDateTime.now().minusDays(1));
        factura.setPrecio(new BigDecimal("25990.00"));
        factura.setRunPropietario("12.345.678-9");
    }

    // ============ TESTS DE GUARDAR ============

    @Test
    void guardarFactura_cuandoTodoEsValido_debeGuardarYRetornarFactura() {
        when(facturaRepository.findByCodigoFactura("FAC-2024-001")).thenReturn(Optional.empty());
        when(propietarioClient.obtenerPropietarioporRun("12.345.678-9")).thenReturn(new PropietarioDTO());
        when(facturaRepository.save(factura)).thenReturn(factura);

        Optional<Factura> resultado = facturaService.guardarFactura(factura);

        assertTrue(resultado.isPresent());
        assertEquals("FAC-2024-001", resultado.get().getCodigoFactura());
        verify(facturaRepository, times(1)).save(factura);
    }

    @Test
    void guardarFactura_cuandoCodigoYaExiste_debeLanzarRuntimeException() {
        when(facturaRepository.findByCodigoFactura("FAC-2024-001")).thenReturn(Optional.of(factura));

        assertThrows(RuntimeException.class, () -> facturaService.guardarFactura(factura));
        verify(propietarioClient, never()).obtenerPropietarioporRun(anyString());
        verify(facturaRepository, never()).save(any());
    }

    @Test
    void guardarFactura_cuandoPropietarioNoExiste_debeLanzarRuntimeException() {
        when(facturaRepository.findByCodigoFactura("FAC-2024-001")).thenReturn(Optional.empty());
        when(propietarioClient.obtenerPropietarioporRun("12.345.678-9")).thenThrow(mock(FeignException.NotFound.class));

        assertThrows(RuntimeException.class, () -> facturaService.guardarFactura(factura));
        verify(facturaRepository, never()).save(any());
    }

    @Test
    void guardarFactura_cuandoFallaLaBaseDeDatos_debeLanzarRuntimeException() {
        when(facturaRepository.findByCodigoFactura("FAC-2024-001")).thenReturn(Optional.empty());
        when(propietarioClient.obtenerPropietarioporRun("12.345.678-9")).thenReturn(new PropietarioDTO());
        when(facturaRepository.save(factura)).thenThrow(new DataAccessException("Error de conexion") {});

        assertThrows(RuntimeException.class, () -> facturaService.guardarFactura(factura));
    }

    // ============ TESTS DE BUSCAR ============

    @Test
    void buscarPorCodigoFactura_cuandoExiste_debeRetornarFactura() {
        when(facturaRepository.findByCodigoFactura("FAC-2024-001")).thenReturn(Optional.of(factura));

        Optional<Factura> resultado = facturaService.buscarPorCodigoFactura("FAC-2024-001");

        assertTrue(resultado.isPresent());
        assertEquals("Consulta veterinaria y vacunacion", resultado.get().getDetalles());
    }

    @Test
    void buscarPorCodigoFactura_cuandoNoExiste_debeRetornarEmpty() {
        when(facturaRepository.findByCodigoFactura("NO-EXISTE")).thenReturn(Optional.empty());

        Optional<Factura> resultado = facturaService.buscarPorCodigoFactura("NO-EXISTE");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorCodigoFactura_cuandoFallaLaConsulta_debeLanzarRuntimeException() {
        when(facturaRepository.findByCodigoFactura(anyString())).thenThrow(new RuntimeException("fallo inesperado"));

        assertThrows(RuntimeException.class, () -> facturaService.buscarPorCodigoFactura("FAC-2024-001"));
    }

    // ============ TESTS DE OBTENER PROPIETARIO ============

    @Test
    void obtenerPropietario_cuandoExiste_debeRetornarPropietario() {
        PropietarioDTO propietarioDTO = new PropietarioDTO();
        propietarioDTO.setRunPropietario("12.345.678-9");
        when(propietarioClient.obtenerPropietarioporRun("12.345.678-9")).thenReturn(propietarioDTO);

        Optional<PropietarioDTO> resultado = facturaService.obtenerPropietario("12.345.678-9");

        assertTrue(resultado.isPresent());
        assertEquals("12.345.678-9", resultado.get().getRunPropietario());
    }

    @Test
    void obtenerPropietario_cuandoFallaLaLlamada_debeRetornarEmpty() {
        when(propietarioClient.obtenerPropietarioporRun("12.345.678-9")).thenThrow(mock(FeignException.NotFound.class));

        Optional<PropietarioDTO> resultado = facturaService.obtenerPropietario("12.345.678-9");

        assertTrue(resultado.isEmpty());
    }

    // ============ TESTS DE DETALLE COMPLETO ============

    @Test
    void obtenerDetalleCompletoFactura_cuandoExiste_debeRetornarDetalleConPropietario() {
        PropietarioDTO propietarioDTO = new PropietarioDTO();
        propietarioDTO.setRunPropietario("12.345.678-9");

        when(facturaRepository.findByCodigoFactura("FAC-2024-001")).thenReturn(Optional.of(factura));
        when(propietarioClient.obtenerPropietarioporRun("12.345.678-9")).thenReturn(propietarioDTO);

        Optional<FacturaDTO> resultado = facturaService.obtenerDetalleCompletoFactura("FAC-2024-001");

        assertTrue(resultado.isPresent());
        assertEquals("FAC-2024-001", resultado.get().getCodigoFactura());
        assertNotNull(resultado.get().getPropietario());
    }

    @Test
    void obtenerDetalleCompletoFactura_cuandoNoExisteLaFactura_debeRetornarEmpty() {
        when(facturaRepository.findByCodigoFactura("NO-EXISTE")).thenReturn(Optional.empty());

        Optional<FacturaDTO> resultado = facturaService.obtenerDetalleCompletoFactura("NO-EXISTE");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerDetalleCompletoFactura_cuandoPropietarioNoDisponible_debeLanzarRuntimeException() {
        when(facturaRepository.findByCodigoFactura("FAC-2024-001")).thenReturn(Optional.of(factura));
        when(propietarioClient.obtenerPropietarioporRun("12.345.678-9")).thenThrow(mock(FeignException.NotFound.class));

        assertThrows(RuntimeException.class, () -> facturaService.obtenerDetalleCompletoFactura("FAC-2024-001"));
    }
}
