package Microservicio.Registro.Service;

import Microservicio.Registro.Modelo.Mascota;
import Microservicio.Registro.Repository.MascotaRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;//Importante para errores relacionados con la base de datos
import org.springframework.stereotype.Service;

@Service
public class MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;

    
    // Guardar
    public Optional<Mascota> GuardarMascota(Mascota mascota) {
        try {
            Optional<Mascota> existe = mascotaRepository.findByCodigoMicrochip(mascota.getCodigoMicrochip());

            if (existe.isPresent()) {
                // Usamos System.err para resaltar que hubo un conflicto 
                System.err.println("El código chip ya está registrado en la base de datos");
                return Optional.empty(); // Retornamos caja vacía
            }
            
            return Optional.of(mascotaRepository.save(mascota));

        } catch (DataAccessException e) { //maneja error relacionado con base de dato
            System.err.println("Error de base de datos al intentar guardar la mascota: " + e.getMessage());
            throw new RuntimeException("Error interno al guardar en la base de datos");
        } catch (Exception e) { //maneja errores generales
            System.err.println("Error inesperado: " + e.getMessage());
            throw new RuntimeException("Error inesperado en el servidor");
        }
    }

    // Buscar por codigo chip
    public Optional<Mascota> buscarPorChip(String codigoMicrochip) {
        try {
            return mascotaRepository.findByCodigoMicrochip(codigoMicrochip);
        } catch (Exception e) {
            System.err.println("Error al buscar mascota por chip: " + e.getMessage());
            throw new RuntimeException("Error al consultar la base de datos");
        }
    }

    
    // Actualizar
    public Mascota ActualizarMascota(String codigoMicrochip, Mascota datosNuevos) {
        try {
            // Buscamos la mascota que ya existe en la BD
            Mascota mascotaEnBD = mascotaRepository.findByCodigoMicrochip(codigoMicrochip).orElse(null);
            
            if (datosNuevos.getNombre() == null || datosNuevos.getNombre().trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre es obligatorio.");
            }

            if (datosNuevos.getEspecie() == null || datosNuevos.getEspecie().trim().isEmpty()) {
                throw new IllegalArgumentException("la especie es obligatorio.");
            }

            if (datosNuevos.getRaza() == null || datosNuevos.getRaza().trim().isEmpty()) {
                throw new IllegalArgumentException("la raza es obligatorio.");
            }

            if (datosNuevos.getEdad() == null ) {
                throw new IllegalArgumentException("la edad es obligatorio.");
            }

            // Validación
            if (mascotaEnBD != null) {
                mascotaEnBD.setNombre(datosNuevos.getNombre());
                mascotaEnBD.setEspecie(datosNuevos.getEspecie());
                mascotaEnBD.setRaza(datosNuevos.getRaza());
                mascotaEnBD.setEdad(datosNuevos.getEdad());
                
                // No se actualizan claves foraneas como el id del propietario

                return mascotaRepository.save(mascotaEnBD);
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            System.err.println("Error de base de datos al actualizar: " + e.getMessage());
            throw new RuntimeException("Error interno al actualizar los datos");
        }
    }

    
    // Eliminar por codigo chip
    public boolean eliminarMascotaExistente(String codigoMicrochip) {
        try {
            Optional<Mascota> mascota = mascotaRepository.findByCodigoMicrochip(codigoMicrochip);

            if (mascota.isPresent()) {
                mascotaRepository.delete(mascota.get());
                return true;
            }
            return false;
            
        } catch (DataAccessException e) {
            System.err.println("Error al intentar eliminar la mascota en la BD: " + e.getMessage());
            throw new RuntimeException("No se puede eliminar la mascota por un problema en la base de datos");
        }
    }
}