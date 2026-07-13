package Microservicio.Registro.Service;

import Microservicio.Registro.Modelo.Propietario;
import Microservicio.Registro.Repository.PropietarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*; //Trae assertTrue, assertEquals, assertNull, etc. Son los que comprueban si el resultado es el esperado
import static org.mockito.Mockito.*; //Trae when(), verify(), any(), never(), times(), doNothing(). Son los que arman y comprueban los mocks

//Esta anotacion le dice a JUnit que active Mockito en esta clase de test, sin esto los @Mock y @InjectMocks de abajo no funcionarian
@ExtendWith(MockitoExtension.class)
class PropietarioServiceTest {

    //@Mock crea una version falsa (simulada) del repositorio, no toca la base de datos real
    //la usamos para decirle "cuando te pregunten esto, responde esto otro" (eso es el when(...))
    @Mock
    private PropietarioRepository propietarioRepository;

    //@InjectMocks crea una instancia real de PropietarioService y le "inyecta" el mock de arriba
    //es decir, el service que probamos usa el repositorio falso sin que nosotros tengamos que hacer new PropietarioService(...) a mano
    @InjectMocks
    private PropietarioService propietarioService;

    //Propietario de prueba que se reutiliza en varios test, se arma una sola vez por test gracias al @BeforeEach
    private Propietario propietario;

    //@BeforeEach hace que este metodo se ejecute antes de CADA @Test
    //esto evita que un test deje "sucia" la variable propietario para el siguiente test
    @BeforeEach
    void setUp() {
        propietario = new Propietario();
        propietario.setRunPropietario("12.345.678-9");
        propietario.setNombre("Alonso");
        propietario.setApellido("Contreras");
        propietario.setCorreo("alonso@gmail.com");
        propietario.setTelefono("+56912345678");
    }

    // ============ TESTS DE GUARDAR ============

    @Test
    void guardarPropietario_cuandoNoExiste_debeRetornarPropietario() {
        //Le decimos al mock: "cuando busquen por este run, responde que no existe nadie (Optional.empty())"
        when(propietarioRepository.findByRunPropietario("12.345.678-9")).thenReturn(Optional.empty());
        //Le decimos al mock: "cuando llamen a save con cualquier Propietario, devuelve nuestro propietario de prueba"
        when(propietarioRepository.save(any(Propietario.class))).thenReturn(propietario);

        //Aqui recien se llama al metodo real del service, que por dentro usa el repositorio falso de arriba
        Optional<Propietario> resultado = propietarioService.guardarPropietario(propietario);

        //Como no existia, el service deberia haber guardado y devuelto la caja llena (Optional con el propietario dentro)
        assertTrue(resultado.isPresent());
        assertEquals("12.345.678-9", resultado.get().getRunPropietario());
        assertEquals("Alonso", resultado.get().getNombre());
    }

    @Test
    void guardarPropietario_cuandoYaExiste_debeRetornarEmpty() {
        //Esta vez el mock dice que SI existe un propietario con ese run
        when(propietarioRepository.findByRunPropietario("12.345.678-9")).thenReturn(Optional.of(propietario));

        Optional<Propietario> resultado = propietarioService.guardarPropietario(propietario);

        //Como ya existia, el service no deberia guardar nada y la caja tiene que venir vacia
        assertTrue(resultado.isEmpty());
        //verify comprueba que un metodo del mock NUNCA se haya llamado, en este caso save(...)
        //sirve para asegurarnos que el service no intento guardar un duplicado
        verify(propietarioRepository, never()).save(any());
    }

    // ============ TESTS DE BUSCAR ============

    @Test
    void buscarPorRun_cuandoExiste_debeRetornarPropietario() {
        when(propietarioRepository.findByRunPropietario("12.345.678-9")).thenReturn(Optional.of(propietario));

        Optional<Propietario> resultado = propietarioService.buscarPorRun("12.345.678-9");

        assertTrue(resultado.isPresent());
        assertEquals("Alonso", resultado.get().getNombre());
    }

    @Test
    void buscarPorRun_cuandoNoExiste_debeRetornarEmpty() {
        //Usamos un run distinto (99.999.999-9) que no coincide con nada para simular que no se encuentra
        when(propietarioRepository.findByRunPropietario("99.999.999-9")).thenReturn(Optional.empty());

        Optional<Propietario> resultado = propietarioService.buscarPorRun("99.999.999-9");

        assertTrue(resultado.isEmpty());
    }

    // ============ TESTS DE ACTUALIZAR ============

    @Test
    void actualizarPropietario_cuandoExiste_debeRetornarPropietarioActualizado() {
        //Estos son los datos "nuevos" que llegarian, por ejemplo, desde un formulario de edicion
        Propietario datoNuevo = new Propietario();
        datoNuevo.setNombre("Pedro");
        datoNuevo.setApellido("Gonzalez");
        datoNuevo.setCorreo("pedro@gmail.com");
        datoNuevo.setTelefono("+56987654321");

        //El mock simula que en la "base de datos" ya existe el propietario original con ese run
        when(propietarioRepository.findByRunPropietario("12.345.678-9")).thenReturn(Optional.of(propietario));
        //Y que al guardar los cambios, el repositorio responde con el propietario (ya actualizado)
        when(propietarioRepository.save(any(Propietario.class))).thenReturn(propietario);

        //Le pedimos al service que actualice el propietario con run "12.345.678-9" usando los datoNuevo
        Propietario resultado = propietarioService.actualizarPropietario("12.345.678-9", datoNuevo);

        //Si encontro al propietario, el resultado no deberia ser null
        assertNotNull(resultado);
        //times(1) comprueba que save se haya llamado exactamente una vez, ni cero ni dos veces
        verify(propietarioRepository, times(1)).save(any(Propietario.class));
    }

    @Test
    void actualizarPropietario_cuandoNoExiste_debeRetornarNull() {
        //Simulamos que no se encuentra ningun propietario con ese run
        when(propietarioRepository.findByRunPropietario("99.999.999-9")).thenReturn(Optional.empty());

        Propietario resultado = propietarioService.actualizarPropietario("99.999.999-9", propietario);

        //Si no lo encontro, el service debe devolver null (asi esta hecho en PropietarioService)
        assertNull(resultado);
        //Y tampoco deberia haber intentado guardar nada
        verify(propietarioRepository, never()).save(any());
    }

    // ============ TESTS DE ELIMINAR ============

    @Test
    void eliminarPropietario_cuandoExiste_debeRetornarTrue() {
        //El mock dice que si existe el propietario con ese run
        when(propietarioRepository.findByRunPropietario("12.345.678-9")).thenReturn(Optional.of(propietario));
        //doNothing() se usa para metodos que no devuelven nada (void), aqui es delete(propietario)
        //le decimos al mock "cuando te llamen a eliminar este propietario, no hagas nada, solo simula que funciono"
        doNothing().when(propietarioRepository).delete(propietario);

        boolean resultado = propietarioService.eliminarPropietarioporRun("12.345.678-9");

        //Como si existia, el service deberia haber eliminado y devuelto true
        assertTrue(resultado);
        //Comprobamos que delete se llamo exactamente 1 vez con ese propietario
        verify(propietarioRepository, times(1)).delete(propietario);
    }

    @Test
    void eliminarPropietario_cuandoNoExiste_debeRetornarFalse() {
        //Simulamos que no existe nadie con ese run
        when(propietarioRepository.findByRunPropietario("99.999.999-9")).thenReturn(Optional.empty());

        boolean resultado = propietarioService.eliminarPropietarioporRun("99.999.999-9");

        //Como no existia, no se elimina nada y el service devuelve false
        assertFalse(resultado);
        //Y confirmamos que nunca se llamo a delete
        verify(propietarioRepository, never()).delete(any());
    }
}
