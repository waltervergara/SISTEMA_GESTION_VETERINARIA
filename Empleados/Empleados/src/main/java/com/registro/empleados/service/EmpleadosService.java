package com.registro.empleados.service;

import java.util.Optional;
import com.registro.empleados.model.Empleados;
import com.registro.empleados.repository.EmpleadosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;


@Service
public class EmpleadosService {
    @Autowired
    private EmpleadosRepository empleadosRepository;

    //Guardar
    public Optional<Empleados> guardarEmpleado(Empleados empleados){
        try{
            //aqui se genera un optional y pregunta si ya hay alguien con este run
            Optional<Empleados> existe = empleadosRepository.findByRunEmpleado(empleados.getRunEmpleado());

            if(existe.isPresent()){//aqui si el optional contiene algo , es decir el run
                return Optional.empty();//entonces devuelve el optional vacio
            }

            return Optional.of(empleadosRepository.save(empleados));//si el if no se cumplio entonces guarda al empleado en la base de datos
        }catch(DataAccessException e){
            System.err.println("Error de base de datos al intentar registrar el empleado" + e.getMessage());
            throw new RuntimeException("Error interno al guardar en la base de datos");
        }catch(Exception e){
            System.err.println("Error inesperado :" + e.getMessage());
            throw new RuntimeException("Error inesperado en el servidor");
        }
    }

    //Buscar por rut
    public Optional<Empleados> buscarPorRun(String runEmpleado){
        try{
            return empleadosRepository.findByRunEmpleado(runEmpleado);
        }catch(Exception e){
            System.err.println("Error al buscar empleado por run: " + e.getMessage());
            throw new RuntimeException("Error al consultar la base de datos");
        }
    }

    //Actualizar
    public Empleados ActualizarEmpleados(String runEmpleado , Empleados empleadosExistentes){

        if (empleadosExistentes.getNombre() == null || empleadosExistentes.getNombre().trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        if (empleadosExistentes.getFecha_nacimiento() == null) {
                throw new IllegalArgumentException("la fecha es obligatorio.");
        }
        
        if (empleadosExistentes.getCargo() == null || empleadosExistentes.getCargo().trim().isEmpty()) {
                throw new IllegalArgumentException("El cargo es obligatorio.");
        }

        if (empleadosExistentes.getGmail() == null || empleadosExistentes.getGmail().trim().isEmpty()) {
                throw new IllegalArgumentException("El gmail es obligatorio.");
        }

        if (empleadosExistentes.getNumero_telefono() == null || empleadosExistentes.getNumero_telefono().trim().isEmpty()) {
                throw new IllegalArgumentException("El numero de telefono es obligatorio.");
        }

        

        try{
            //buscamos por run , al ser dato unico
            Empleados empleadosNuevo = empleadosRepository.findByRunEmpleado(runEmpleado).orElse(null);//si tencuentra 

            //validacion
            if (empleadosNuevo !=null) {
            empleadosNuevo.setNombre(empleadosExistentes.getNombre());
            empleadosNuevo.setFecha_nacimiento(empleadosExistentes.getFecha_nacimiento());
            empleadosNuevo.setCargo(empleadosExistentes.getCargo());
            empleadosNuevo.setGmail(empleadosExistentes.getGmail());
            empleadosNuevo.setNumero_telefono(empleadosExistentes.getNumero_telefono());
            return empleadosRepository.save(empleadosNuevo);
            }else{
            return null;
            }
        }catch(DataAccessException e){
            System.err.println("Error en la base de datos al acutualizar : " + e.getMessage());
            throw new RuntimeException("Error interno al actualizar los datos");
        }

    }


    //Metodo para eliminar por runn
    public boolean eliminarEmpleadosRun(String runEmpleado){
        try{
            //Para ver primero si existe primero
            Empleados empleadosExistentes = empleadosRepository.findByRunEmpleado(runEmpleado).orElse(null);

            //Validacion
            if (empleadosExistentes != null) {
                //si existe lo borra
                empleadosRepository.delete(empleadosExistentes);
                return true;
            }else{
                //no existe , no se borra
                return false;
            }
        }catch(DataAccessException e){
            //manejo de error en base de datos
            System.err.println("Error al intentar eliminar propietario en la Base de datos: " + e.getMessage());
            throw new RuntimeException("No se puede elimar el propietario porquie esta en uso la base de datos fallo");
        }
    }

}
