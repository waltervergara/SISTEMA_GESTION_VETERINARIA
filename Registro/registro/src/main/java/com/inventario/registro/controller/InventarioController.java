package com.inventario.registro.controller;

import com.inventario.registro.model.Inventario;
import com.inventario.registro.service.InventarioService;

import jakarta.validation.Valid;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/registro/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;


    // CREAR: POST a /api/v1/registro/empleados
    @PostMapping
    public ResponseEntity<?> guardarInvetario(@Valid@RequestBody Inventario inventario) {
       try{
            Optional<Inventario> nuevoInventario = inventarioService.guardarInventario(inventario);
        
            // Validación: Si el service devolvió null, el RUN ya estaba registrado
            if(nuevoInventario.isPresent()){
                return ResponseEntity.status(HttpStatus.CREATED).body(nuevoInventario.get());
            } else{
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El Inventario ya existe");
            }
       } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
       }
    }

    
    @GetMapping("/{nombre}")
    public ResponseEntity<?> buscarPorNombre(@PathVariable String nombre) {
       try{
            Optional<Inventario> inventarioEncontrado = inventarioService.buscarPorNombre(nombre);

            if (inventarioEncontrado.isPresent()) {
                return ResponseEntity.ok(inventarioEncontrado.get()); // Devuelve 200 OK
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Inventario no encontrado"); // Devuelve 404 Not Found
            }
       }catch (RuntimeException e){
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
       }
    }

    // ACTUALIZAR: PUT a /api/v1/registro/empleados/{run}
    @PutMapping("/{nombre}")
    public ResponseEntity<?> actualizarInventario(@PathVariable String nombre, @Valid@RequestBody Inventario nuevoInventario) {
        try{
            Inventario inventarioActualizado = inventarioService.actualizarInventario(nombre, nuevoInventario);

            if (inventarioActualizado != null) {
                return ResponseEntity.ok(inventarioActualizado); // Devuelve 200 OK
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se a encontrado el inventario para revizar"); // Devuelve 404 Not Found
            }
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ELIMINAR: DELETE a /api/v1/registro/empleados/{run}
    @DeleteMapping("/{nombre}")
    public ResponseEntity<String> eliminarPorNombre(@PathVariable String nombre) {
        try{
            boolean fueEliminado = inventarioService.eliminarInventarioNombre(nombre);

            if (fueEliminado) {
                return ResponseEntity.ok("inventario con nombre " + nombre + " fue eliminado correctamente."); // Devuelve 200 OK
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se a logrado encontrar el nombre para eliminar"); // Devuelve 404 Not Found
            }
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //http://localhost:8083/api/v1/registro/inventario

}
