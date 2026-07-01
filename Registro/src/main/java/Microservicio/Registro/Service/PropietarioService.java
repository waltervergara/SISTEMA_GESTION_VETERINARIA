package Microservicio.Registro.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;//Importante para errores relacionados con la base de datos
import org.springframework.stereotype.Service;

import Microservicio.Registro.Modelo.Propietario;
import Microservicio.Registro.Repository.PropietarioRepository;

@Service
public class PropietarioService {

    @Autowired
    private PropietarioRepository propietarioRepository;

    // Guardar
    public Optional<Propietario> guardarPropietario(Propietario propietario) {
        try {
            //El optional es raro , por lo que entendi es una caja que puede estar llena o vacia
            Optional<Propietario> existe = propietarioRepository.findByRunPropietario(propietario.getRunPropietario());
            
            if (existe.isPresent()) { //isPresent es bueno que si se encuentra
                return Optional.empty();//siguiendo con lo de antes si aqui enviamos una caja vacia ya que existe el dato
            }
            
            // Intentamos guardar
            return Optional.of(propietarioRepository.save(propietario));//Aqui es cuando llenamos la caja y la enviamos

        } catch (DataAccessException e) {
            //Los DataAccessExeption son manejos de errores relacionados con la base de datos
            System.err.println("Error de base de datos al intentar guardar el propietario: " + e.getMessage());//por lo que vi e.getMessage es donde te dice directamente que fallo
            // Por lo que vi esto es como un boton de panico para el controller donde detecta altiro que algo salio mal y bloquea el codigo hasta aqui mostrando el mensaje
            throw new RuntimeException("Error interno al guardar en la base de datos");
        } catch (Exception e) {
            //En caso de tener cualquier otro error
            //System.err.printl = es como el que ocupabamos pero este esta destinado para errores cono aqui
            System.err.println("Error inesperado: " + e.getMessage());
            throw new RuntimeException("Error inesperado en el servidor");
        }
    }

    // Buscar por rut
    public Optional<Propietario> buscarPorRun(String run) {
        try {
            return propietarioRepository.findByRunPropietario(run);
        } catch (Exception e) {
            System.err.println("Error al buscar propietario por RUN: " + e.getMessage());
            throw new RuntimeException("Error al consultar la base de datos");
        }
    }

    // Actualizar
    public Propietario actualizarPropietario(String run_propietario, Propietario datosNuevos) {
        
       if (datosNuevos == null) {
        throw new IllegalArgumentException("Los datos no pueden ser nulos.");
       }

       if (datosNuevos.getNombre() == null || datosNuevos.getNombre().trim().isEmpty()) {
        throw new IllegalArgumentException("El nombre es obligatorio.");
       }

       if (datosNuevos.getApellido() == null || datosNuevos.getApellido().trim().isEmpty()) {
        throw new IllegalArgumentException("El apellido es obligatorio.");
       }

       if (datosNuevos.getCorreo() == null || datosNuevos.getCorreo().trim().isEmpty()) {
        throw new IllegalArgumentException("El correo es obligatorio.");
       }

       if (datosNuevos.getTelefono() == null || datosNuevos.getTelefono().trim().isEmpty()) {
        throw new IllegalArgumentException("El teléfono es obligatorio.");
       }
     




        try {
            Propietario propietarioEnBD = propietarioRepository.findByRunPropietario(run_propietario).orElse(null);

            if (propietarioEnBD != null) {
                propietarioEnBD.setNombre(datosNuevos.getNombre());
                propietarioEnBD.setApellido(datosNuevos.getApellido());
                propietarioEnBD.setCorreo(datosNuevos.getCorreo());
                propietarioEnBD.setTelefono(datosNuevos.getTelefono());
                
                return propietarioRepository.save(propietarioEnBD);
            } else {
                return null; // No lo encontró
            }
        } catch (DataAccessException e) {
            System.err.println("Error de base de datos al actualizar: " + e.getMessage());
            throw new RuntimeException("Error interno al actualizar los datos");
        }
    }

    // Eliminar por run
    public boolean eliminarPropietarioporRun(String run_propietario) {

        try {
            //Forma curiosa de buscar , propietarioRepository.findByrun(run) , esta parte es la caja y el orElse(null); es como si encuentras al propietario y entregamelo 
            //para despues guardarlo en la variable propietario existente pero si esta vacio no dara error si no que dara null como una variable
            Propietario propietarioExistente = propietarioRepository.findByRunPropietario(run_propietario).orElse(null);

            //si encuentra el en la caja lo elimina
            if (propietarioExistente != null) {
                //si existe lo elimina y retorna true
                propietarioRepository.delete(propietarioExistente);
                return true;
            } else {
                //no lo encuentra por lo que no lo borra
                return false;
            }
        } catch (DataAccessException e) {
            System.err.println("Error al intentar eliminar propietario en la BD: " + e.getMessage());
            throw new RuntimeException("No se puede eliminar el propietario porque está en uso o la BD falló");
        }
    }
}