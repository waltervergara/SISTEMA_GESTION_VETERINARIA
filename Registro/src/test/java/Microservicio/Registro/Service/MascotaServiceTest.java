package Microservicio.Registro.Service;

import Microservicio.Registro.Modelo.Mascota;
import Microservicio.Registro.Modelo.Propietario;
import Microservicio.Registro.Repository.MascotaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*; //assertTrue, assertEquals, assertNull, etc.
import static org.mockito.Mockito.*; //when(), verify(), any(), never(), times(), doNothing()

//Activa Mockito en esta clase, sin esto @Mock y @InjectMocks no funcionan
@ExtendWith(MockitoExtension.class)
class MascotaServiceTest {

    //Version falsa del repositorio, no toca la base de datos real
    @Mock
    private MascotaRepository mascotaRepository;

    //Instancia real de MascotaService con el mock de arriba inyectado adentro
    @InjectMocks
    private MascotaService mascotaService;

    private Mascota mascota;
    private Propietario propietario;

    //Se ejecuta antes de cada @Test para que ningun test deje datos "sucios" para el siguiente
    @BeforeEach
    void setUp() {
        //La mascota necesita un propietario (relacion @ManyToOne), armamos uno de prueba
        propietario = new Propietario();
        propietario.setRunPropietario("12.345.678-9");
        propietario.setNombre("Alonso");
        propietario.setApellido("Contreras");
        propietario.setCorreo("alonso@gmail.com");
        propietario.setTelefono("+56912345678");

        mascota = new Mascota();
        mascota.setCodigoMicrochip("985121012345");
        mascota.setNombre("Firulais");
        mascota.setEdad(3);
        mascota.setAño_nacimiento(2022);
        mascota.setEspecie("Perro");
        mascota.setRaza("Bulldog");
        mascota.setPropietario(propietario);
    }

    // ============ TESTS DE GUARDAR ============

    @Test
    void guardarMascota_cuandoNoExiste_debeRetornarMascota() {
        //El mock dice que no hay ninguna mascota con ese chip todavia
        when(mascotaRepository.findByCodigoMicrochip("985121012345")).thenReturn(Optional.empty());
        //Y que al guardar, el repositorio responde con nuestra mascota de prueba
        when(mascotaRepository.save(any(Mascota.class))).thenReturn(mascota);

        Optional<Mascota> resultado = mascotaService.GuardarMascota(mascota);

        //Como no existia, deberia guardarse y devolver la caja llena
        assertTrue(resultado.isPresent());
        assertEquals("985121012345", resultado.get().getCodigoMicrochip());
        assertEquals("Firulais", resultado.get().getNombre());
    }

    @Test
    void guardarMascota_cuandoYaExiste_debeRetornarEmpty() {
        //Simulamos que ya existe una mascota registrada con ese mismo chip
        when(mascotaRepository.findByCodigoMicrochip("985121012345")).thenReturn(Optional.of(mascota));

        Optional<Mascota> resultado = mascotaService.GuardarMascota(mascota);

        //No deberia guardar un chip duplicado, la caja viene vacia
        assertTrue(resultado.isEmpty());
        verify(mascotaRepository, never()).save(any());
    }

    // ============ TESTS DE BUSCAR ============

    @Test
    void buscarPorChip_cuandoExiste_debeRetornarMascota() {
        when(mascotaRepository.findByCodigoMicrochip("985121012345")).thenReturn(Optional.of(mascota));

        Optional<Mascota> resultado = mascotaService.buscarPorChip("985121012345");

        assertTrue(resultado.isPresent());
        assertEquals("Firulais", resultado.get().getNombre());
    }

    @Test
    void buscarPorChip_cuandoNoExiste_debeRetornarEmpty() {
        //Un chip que no coincide con nada, para simular que no se encuentra
        when(mascotaRepository.findByCodigoMicrochip("000000000000")).thenReturn(Optional.empty());

        Optional<Mascota> resultado = mascotaService.buscarPorChip("000000000000");

        assertTrue(resultado.isEmpty());
    }

    // ============ TESTS DE ACTUALIZAR ============

    @Test
    void actualizarMascota_cuandoExiste_debeRetornarMascotaActualizada() {
        //Datos nuevos validos (nombre, especie, raza y edad), si faltara alguno el service lanzaria IllegalArgumentException
        Mascota datoNuevo = new Mascota();
        datoNuevo.setNombre("Rocky");
        datoNuevo.setEspecie("Gato");
        datoNuevo.setRaza("Siames");
        datoNuevo.setEdad(5);

        when(mascotaRepository.findByCodigoMicrochip("985121012345")).thenReturn(Optional.of(mascota));
        when(mascotaRepository.save(any(Mascota.class))).thenReturn(mascota);

        Mascota resultado = mascotaService.ActualizarMascota("985121012345", datoNuevo);

        assertNotNull(resultado);
        verify(mascotaRepository, times(1)).save(any(Mascota.class));
    }

    @Test
    void actualizarMascota_cuandoNoExiste_debeRetornarNull() {
        //No se encuentra ninguna mascota con ese chip
        when(mascotaRepository.findByCodigoMicrochip("000000000000")).thenReturn(Optional.empty());

        //Usamos "mascota" (que ya tiene todos los campos validos) para pasar las validaciones y llegar al caso "no existe"
        Mascota resultado = mascotaService.ActualizarMascota("000000000000", mascota);

        assertNull(resultado);
        verify(mascotaRepository, never()).save(any());
    }

    // ============ TESTS DE ELIMINAR ============

    @Test
    void eliminarMascota_cuandoExiste_debeRetornarTrue() {
        when(mascotaRepository.findByCodigoMicrochip("985121012345")).thenReturn(Optional.of(mascota));
        //delete(...) no devuelve nada (void), doNothing() simula que se ejecuto sin errores
        doNothing().when(mascotaRepository).delete(mascota);

        boolean resultado = mascotaService.eliminarMascotaExistente("985121012345");

        assertTrue(resultado);
        verify(mascotaRepository, times(1)).delete(mascota);
    }

    @Test
    void eliminarMascota_cuandoNoExiste_debeRetornarFalse() {
        when(mascotaRepository.findByCodigoMicrochip("000000000000")).thenReturn(Optional.empty());

        boolean resultado = mascotaService.eliminarMascotaExistente("000000000000");

        assertFalse(resultado);
        verify(mascotaRepository, never()).delete(any());
    }
}
