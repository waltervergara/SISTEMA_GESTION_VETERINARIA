package com.inventario.inventario.service;

import com.inventario.inventario.model.Inventario;
import com.inventario.inventario.repository.InventarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*; //assertTrue, assertEquals, assertNull, etc.
import static org.mockito.Mockito.*; //when(), verify(), any(), never(), times(), doNothing()

//Activa Mockito en esta clase, sin esto @Mock y @InjectMocks no funcionan
@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    //Repositorio simulado, no toca la base de datos real
    @Mock
    private InventarioRepository inventarioRepository;

    //Instancia real de InventarioService con el mock de arriba inyectado adentro
    @InjectMocks
    private InventarioService inventarioService;

    private Inventario inventario;

    //Se ejecuta antes de cada @Test para que ningun test deje datos "sucios" para el siguiente
    @BeforeEach
    void setUp() {
        inventario = new Inventario();
        inventario.setNombre("Amoxicilina 500mg");
        inventario.setFecha_elaboracion(LocalDate.of(2024, 1, 15));
        inventario.setVencimiento("2026-01-15");
        inventario.setStock(150L);
        inventario.setDescripcion("Antibiotico de amplio espectro para uso veterinario");
        inventario.setPrecio(5990L);
    }

    // ============ TESTS DE GUARDAR ============

    @Test
    void guardarInventario_cuandoNoExiste_debeRetornarInventario() {
        when(inventarioRepository.findByNombre("Amoxicilina 500mg")).thenReturn(Optional.empty());
        when(inventarioRepository.save(inventario)).thenReturn(inventario);

        Optional<Inventario> resultado = inventarioService.guardarInventario(inventario);

        assertTrue(resultado.isPresent());
        assertEquals("Amoxicilina 500mg", resultado.get().getNombre());
        verify(inventarioRepository, times(1)).save(inventario);
    }

    @Test
    void guardarInventario_cuandoYaExiste_debeRetornarEmpty() {
        when(inventarioRepository.findByNombre("Amoxicilina 500mg")).thenReturn(Optional.of(inventario));

        Optional<Inventario> resultado = inventarioService.guardarInventario(inventario);

        assertTrue(resultado.isEmpty());
        verify(inventarioRepository, never()).save(any());
    }

    @Test
    void guardarInventario_cuandoFallaLaBaseDeDatos_debeLanzarRuntimeException() {
        when(inventarioRepository.findByNombre("Amoxicilina 500mg")).thenReturn(Optional.empty());
        when(inventarioRepository.save(inventario)).thenThrow(new DataAccessException("Error de conexion") {});

        assertThrows(RuntimeException.class, () -> inventarioService.guardarInventario(inventario));
    }

    // ============ TESTS DE BUSCAR ============

    @Test
    void buscarPorNombre_cuandoExiste_debeRetornarInventario() {
        when(inventarioRepository.findByNombre("Amoxicilina 500mg")).thenReturn(Optional.of(inventario));

        Optional<Inventario> resultado = inventarioService.buscarPorNombre("Amoxicilina 500mg");

        assertTrue(resultado.isPresent());
        assertEquals(150L, resultado.get().getStock());
    }

    @Test
    void buscarPorNombre_cuandoNoExiste_debeRetornarEmpty() {
        when(inventarioRepository.findByNombre("no-existe")).thenReturn(Optional.empty());

        Optional<Inventario> resultado = inventarioService.buscarPorNombre("no-existe");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorNombre_cuandoFallaLaConsulta_debeLanzarRuntimeException() {
        when(inventarioRepository.findByNombre(anyString())).thenThrow(new RuntimeException("fallo inesperado"));

        assertThrows(RuntimeException.class, () -> inventarioService.buscarPorNombre("Amoxicilina 500mg"));
    }

    // ============ TESTS DE ACTUALIZAR ============

    @Test
    void actualizarInventario_cuandoExisteYDatosValidos_debeRetornarInventarioActualizado() {
        Inventario datoNuevo = new Inventario();
        datoNuevo.setNombre("Amoxicilina 500mg");
        datoNuevo.setFecha_elaboracion(LocalDate.of(2024, 3, 1));
        datoNuevo.setVencimiento("2026-03-01");
        datoNuevo.setStock(200L);
        datoNuevo.setDescripcion("Nuevo lote recibido");
        datoNuevo.setPrecio(6500L);

        when(inventarioRepository.findByNombre("Amoxicilina 500mg")).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Inventario resultado = inventarioService.actualizarInventario("Amoxicilina 500mg", datoNuevo);

        assertNotNull(resultado);
        assertEquals(200L, resultado.getStock());
        assertEquals("Nuevo lote recibido", resultado.getDescripcion());
        assertEquals(6500L, resultado.getPrecio());
    }

    @Test
    void actualizarInventario_cuandoNoExiste_debeRetornarNull() {
        when(inventarioRepository.findByNombre("no-existe")).thenReturn(Optional.empty());

        Inventario resultado = inventarioService.actualizarInventario("no-existe", inventario);

        assertNull(resultado);
        verify(inventarioRepository, never()).save(any());
    }

    // Los siguientes tests prueban las validaciones de campos obligatorios que hace el
    // service ANTES de tocar el repositorio (por eso no hace falta configurar ningun mock aqui)

    @Test
    void actualizarInventario_cuandoNombreEsVacio_debeLanzarIllegalArgumentException() {
        inventario.setNombre(" ");

        assertThrows(IllegalArgumentException.class,
                () -> inventarioService.actualizarInventario("Amoxicilina 500mg", inventario));
    }

    @Test
    void actualizarInventario_cuandoFechaEsNula_debeLanzarIllegalArgumentException() {
        inventario.setFecha_elaboracion(null);

        assertThrows(IllegalArgumentException.class,
                () -> inventarioService.actualizarInventario("Amoxicilina 500mg", inventario));
    }

    @Test
    void actualizarInventario_cuandoStockEsNulo_debeLanzarIllegalArgumentException() {
        inventario.setStock(null);

        assertThrows(IllegalArgumentException.class,
                () -> inventarioService.actualizarInventario("Amoxicilina 500mg", inventario));
    }

    @Test
    void actualizarInventario_cuandoVencimientoEsVacio_debeLanzarIllegalArgumentException() {
        inventario.setVencimiento("");

        assertThrows(IllegalArgumentException.class,
                () -> inventarioService.actualizarInventario("Amoxicilina 500mg", inventario));
    }

    @Test
    void actualizarInventario_cuandoDescripcionEsVacia_debeLanzarIllegalArgumentException() {
        inventario.setDescripcion("");

        assertThrows(IllegalArgumentException.class,
                () -> inventarioService.actualizarInventario("Amoxicilina 500mg", inventario));
    }

    @Test
    void actualizarInventario_cuandoPrecioEsNulo_debeLanzarIllegalArgumentException() {
        inventario.setPrecio(null);

        assertThrows(IllegalArgumentException.class,
                () -> inventarioService.actualizarInventario("Amoxicilina 500mg", inventario));
    }

    // ============ TESTS DE ELIMINAR ============

    @Test
    void eliminarInventarioNombre_cuandoExiste_debeRetornarTrue() {
        when(inventarioRepository.findByNombre("Amoxicilina 500mg")).thenReturn(Optional.of(inventario));

        boolean resultado = inventarioService.eliminarInventarioNombre("Amoxicilina 500mg");

        assertTrue(resultado);
        verify(inventarioRepository, times(1)).delete(inventario);
    }

    @Test
    void eliminarInventarioNombre_cuandoNoExiste_debeRetornarFalse() {
        when(inventarioRepository.findByNombre("no-existe")).thenReturn(Optional.empty());

        boolean resultado = inventarioService.eliminarInventarioNombre("no-existe");

        assertFalse(resultado);
        verify(inventarioRepository, never()).delete(any());
    }

    @Test
    void eliminarInventarioNombre_cuandoFallaLaBaseDeDatos_debeLanzarRuntimeException() {
        when(inventarioRepository.findByNombre("Amoxicilina 500mg")).thenReturn(Optional.of(inventario));
        doThrow(new DataAccessException("Error de conexion") {}).when(inventarioRepository).delete(inventario);

        assertThrows(RuntimeException.class, () -> inventarioService.eliminarInventarioNombre("Amoxicilina 500mg"));
    }
}
