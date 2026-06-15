package com.inventario.registro.service;
import com.inventario.registro.model.Inventario;
import com.inventario.registro.repository.InventarioRepository;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;


@Service
public class InventarioService {
    
    @Autowired
    private InventarioRepository inventarioRepository;

    //Guardar
    public Optional<Inventario> guardarInventario(Inventario inventario){
        try{
            Optional<Inventario> existe = inventarioRepository.findByNombre(inventario.getNombre());

            if(existe.isPresent()){
                return Optional.empty();
            }

            return Optional.of(inventarioRepository.save(inventario));
        }catch(DataAccessException e){
            System.err.println("Error de base de datos al intentar registrar inventario" + e.getMessage());
            throw new RuntimeException("Error interno al guardar en la base de datos");
        }catch(Exception e){
            System.err.println("Error inesperado :" + e.getMessage());
            throw new RuntimeException("Error inesperado en el servidor");
        }
    }

    //Buscar por nombre
    public Optional<Inventario> buscarPorNombre(String nombre){
        try{
            return inventarioRepository.findByNombre(nombre);
        }catch(Exception e){
            System.err.println("Error al buscar Inventario por nombre: " + e.getMessage());
            throw new RuntimeException("Error al consultar la base de datos");
        }
    }

    //Actualizar
    public Inventario actualizarInventario(String nombre , Inventario inventarioExistente){

        try{
            //buscamos por nombre primero para que sea simple
            Inventario inventarioNuevo = inventarioRepository.findByNombre(nombre).orElse(null);//si tencuentra 

            //validacion
            if (inventarioNuevo !=null) {
            inventarioNuevo.setNombre(inventarioExistente.getNombre());
            inventarioNuevo.setStock(inventarioExistente.getStock());
            inventarioNuevo.setFecha_elaboracion(inventarioExistente.getFecha_elaboracion());
            inventarioNuevo.setVencimiento(inventarioExistente.getVencimiento());
            inventarioNuevo.setDescripcion(inventarioExistente.getDescripcion());
            inventarioNuevo.setPrecio(inventarioExistente.getPrecio());
            return inventarioRepository.save(inventarioNuevo);
            }else{
            return null;
            }
        }catch(DataAccessException e){
            System.err.println("Error en la base de datos al acutualizar : " + e.getMessage());
            throw new RuntimeException("Error interno al actualizar los datos");
        }

    }


    //Metodo para eliminar por nombre
    public boolean eliminarInventarioNombre(String nombre){
        try{
            //Para ver primero si existe primero
            Inventario inventarioExistente = inventarioRepository.findByNombre(nombre).orElse(null);

            //Validacion
            if (inventarioExistente != null) {
                inventarioRepository.delete(inventarioExistente);
                return true;
            }else{
                return false;
            }
        }catch(DataAccessException e){
            System.err.println("Error al intentar eliminar inventario en la Base de datos: " + e.getMessage());
            throw new RuntimeException("No se puede eliminar el inventario  porquie esta en uso la base de datos fallo");
        }
    }

}
