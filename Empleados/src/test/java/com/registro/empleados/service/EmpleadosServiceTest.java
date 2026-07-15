package com.registro.empleados.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import com.registro.empleados.model.Empleados;
import com.registro.empleados.repository.EmpleadosRepository;

// Habilita las anotaciones de Mockito (@Mock, @InjectMocks) sin necesidad de levantar Spring
@ExtendWith(MockitoExtension.class)
class EmpleadosServiceTest {

    // Repositorio simulado (mock): no toca la base de datos real, solo devuelve lo que le indicamos con "when(...)"
    @Mock
    private EmpleadosRepository empleadosRepository;

    // Mockito crea el EmpleadosService real e inyecta el mock de arriba en su campo @Autowired
    @InjectMocks
    private EmpleadosService empleadosService;

    // Empleado de prueba reutilizado en varios tests
    private Empleados empleado;

    // Se ejecuta antes de cada @Test para dejar un empleado "limpio" y evitar que un test contamine a otro
    @BeforeEach
    void setUp() {
        empleado = new Empleados(
                "98.765.432-1",
                "Walter Vergara",
                LocalDate.of(1990, 10, 1),
                "Secretario",
                "walter@gmail.com",
                "+56912345678");
    }

    // ---------- guardarEmpleado ----------

    @Test
    void guardarEmpleado_deberiaGuardarCuandoRunNoExiste() {
        // Simula que el repositorio NO encuentra a nadie con ese run (no hay duplicado)
        when(empleadosRepository.findByRunEmpleado(empleado.getRunEmpleado())).thenReturn(Optional.empty());
        // Simula que al guardar, el repositorio devuelve el mismo empleado
        when(empleadosRepository.save(empleado)).thenReturn(empleado);

        Optional<Empleados> resultado = empleadosService.guardarEmpleado(empleado);

        // El service debe devolver el empleado guardado dentro de un Optional
        assertTrue(resultado.isPresent());
        assertEquals(empleado, resultado.get());
        // Verifica que efectivamente se llamó a save() exactamente una vez
        verify(empleadosRepository, times(1)).save(empleado);
    }

    @Test
    void guardarEmpleado_deberiaRetornarVacioCuandoRunYaExiste() {
        // Simula que el repositorio SI encuentra un empleado con ese run (duplicado)
        when(empleadosRepository.findByRunEmpleado(empleado.getRunEmpleado())).thenReturn(Optional.of(empleado));

        Optional<Empleados> resultado = empleadosService.guardarEmpleado(empleado);

        // Como ya existe el run, el service debe rechazar el guardado devolviendo Optional vacio
        assertTrue(resultado.isEmpty());
        // Y nunca debe haber llamado a save(), porque la regla de negocio corta antes
        verify(empleadosRepository, never()).save(any());
    }

    @Test
    void guardarEmpleado_deberiaLanzarRuntimeExceptionCuandoFallaLaBaseDeDatos() {
        // Simula que la consulta al repositorio explota con un error de base de datos
        when(empleadosRepository.findByRunEmpleado(empleado.getRunEmpleado()))
                .thenThrow(new DataAccessException("Error de conexion") {});

        // El service debe atrapar el DataAccessException y relanzarlo como RuntimeException propio
        assertThrows(RuntimeException.class, () -> empleadosService.guardarEmpleado(empleado));
    }

    // ---------- buscarPorRun ----------

    @Test
    void buscarPorRun_deberiaRetornarEmpleadoCuandoExiste() {
        when(empleadosRepository.findByRunEmpleado(empleado.getRunEmpleado())).thenReturn(Optional.of(empleado));

        Optional<Empleados> resultado = empleadosService.buscarPorRun(empleado.getRunEmpleado());

        // Camino feliz: el service simplemente devuelve lo que el repositorio encontro
        assertTrue(resultado.isPresent());
        assertEquals(empleado, resultado.get());
    }

    @Test
    void buscarPorRun_deberiaRetornarVacioCuandoNoExiste() {
        when(empleadosRepository.findByRunEmpleado("no-existe")).thenReturn(Optional.empty());

        Optional<Empleados> resultado = empleadosService.buscarPorRun("no-existe");

        // Si el repositorio no encuentra nada, el service tampoco debe inventarse un resultado
        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorRun_deberiaLanzarRuntimeExceptionCuandoFallaLaConsulta() {
        // anyString() porque no importa que run se pase, cualquier llamada debe fallar
        when(empleadosRepository.findByRunEmpleado(anyString())).thenThrow(new RuntimeException("fallo inesperado"));

        // El service debe envolver cualquier excepcion de la consulta en su propia RuntimeException
        assertThrows(RuntimeException.class, () -> empleadosService.buscarPorRun(empleado.getRunEmpleado()));
    }

    // ---------- ActualizarEmpleados ----------

    @Test
    void actualizarEmpleados_deberiaActualizarCuandoExisteYDatosSonValidos() {
        // Datos "nuevos" que llegarian, por ejemplo, desde un formulario de edicion
        Empleados datosNuevos = new Empleados(
                empleado.getRunEmpleado(),
                "Nuevo Nombre",
                LocalDate.of(1995, 5, 20),
                "Veterinario",
                "nuevo@gmail.com",
                "+56987654321");

        // El repositorio encuentra al empleado original que se va a actualizar
        when(empleadosRepository.findByRunEmpleado(empleado.getRunEmpleado())).thenReturn(Optional.of(empleado));
        // save() devuelve el mismo objeto que se le paso (simula que la BD lo persistio tal cual)
        when(empleadosRepository.save(any(Empleados.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Empleados resultado = empleadosService.ActualizarEmpleados(empleado.getRunEmpleado(), datosNuevos);

        // Verifica que cada campo del empleado original quedo pisado con los valores nuevos
        assertEquals("Nuevo Nombre", resultado.getNombre());
        assertEquals("Veterinario", resultado.getCargo());
        assertEquals("nuevo@gmail.com", resultado.getGmail());
        assertEquals("+56987654321", resultado.getNumero_telefono());
    }

    @Test
    void actualizarEmpleados_deberiaRetornarNullCuandoNoExiste() {
        // No hay ningun empleado con ese run en la "base de datos" simulada
        when(empleadosRepository.findByRunEmpleado(empleado.getRunEmpleado())).thenReturn(Optional.empty());

        Empleados resultado = empleadosService.ActualizarEmpleados(empleado.getRunEmpleado(), empleado);

        // Si no existe, el service devuelve null en vez de lanzar excepcion o crear uno nuevo
        assertNull(resultado);
    }

    // Los siguientes 5 tests prueban las validaciones de campos obligatorios que hace el
    // service ANTES de tocar el repositorio (por eso no hace falta configurar ningun mock aqui)

    @Test
    void actualizarEmpleados_deberiaLanzarExcepcionCuandoNombreEsVacio() {
        empleado.setNombre(" "); // solo espacios en blanco cuenta como "vacio"

        assertThrows(IllegalArgumentException.class,
                () -> empleadosService.ActualizarEmpleados(empleado.getRunEmpleado(), empleado));
    }

    @Test
    void actualizarEmpleados_deberiaLanzarExcepcionCuandoFechaEsNula() {
        empleado.setFecha_nacimiento(null);

        assertThrows(IllegalArgumentException.class,
                () -> empleadosService.ActualizarEmpleados(empleado.getRunEmpleado(), empleado));
    }

    @Test
    void actualizarEmpleados_deberiaLanzarExcepcionCuandoCargoEsVacio() {
        empleado.setCargo("");

        assertThrows(IllegalArgumentException.class,
                () -> empleadosService.ActualizarEmpleados(empleado.getRunEmpleado(), empleado));
    }

    @Test
    void actualizarEmpleados_deberiaLanzarExcepcionCuandoGmailEsVacio() {
        empleado.setGmail("");

        assertThrows(IllegalArgumentException.class,
                () -> empleadosService.ActualizarEmpleados(empleado.getRunEmpleado(), empleado));
    }

    @Test
    void actualizarEmpleados_deberiaLanzarExcepcionCuandoTelefonoEsVacio() {
        empleado.setNumero_telefono("");

        assertThrows(IllegalArgumentException.class,
                () -> empleadosService.ActualizarEmpleados(empleado.getRunEmpleado(), empleado));
    }

    // ---------- eliminarEmpleadosRun ----------

    @Test
    void eliminarEmpleadosRun_deberiaEliminarCuandoExiste() {
        when(empleadosRepository.findByRunEmpleado(empleado.getRunEmpleado())).thenReturn(Optional.of(empleado));

        boolean resultado = empleadosService.eliminarEmpleadosRun(empleado.getRunEmpleado());

        // Si el empleado existe, se elimina y el service confirma con true
        assertTrue(resultado);
        // Ademas confirma que se llamo a delete() con el empleado correcto
        verify(empleadosRepository, times(1)).delete(empleado);
    }

    @Test
    void eliminarEmpleadosRun_deberiaRetornarFalseCuandoNoExiste() {
        when(empleadosRepository.findByRunEmpleado("no-existe")).thenReturn(Optional.empty());

        boolean resultado = empleadosService.eliminarEmpleadosRun("no-existe");

        // Si no existe, no hay nada que borrar: el service devuelve false
        assertFalse(resultado);
        // Y jamas debe intentar llamar a delete()
        verify(empleadosRepository, never()).delete(any());
    }

    @Test
    void eliminarEmpleadosRun_deberiaLanzarRuntimeExceptionCuandoFallaLaBaseDeDatos() {
        when(empleadosRepository.findByRunEmpleado(empleado.getRunEmpleado()))
                .thenThrow(new DataAccessException("Error de conexion") {});

        // Igual que en guardar/buscar, cualquier error de BD se traduce a RuntimeException propia
        assertThrows(RuntimeException.class, () -> empleadosService.eliminarEmpleadosRun(empleado.getRunEmpleado()));
    }
}
