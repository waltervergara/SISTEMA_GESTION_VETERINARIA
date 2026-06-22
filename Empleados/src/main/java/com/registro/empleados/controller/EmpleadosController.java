package com.registro.empleados.controller;

import com.registro.empleados.model.Empleados; // O Empleado si lo cambiaste a singular
import com.registro.empleados.service.EmpleadosService;

import jakarta.validation.Valid;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/registro/empleados")
public class EmpleadosController {
    
    @Autowired
    private EmpleadosService empleadosService;

    //POST 
    @PostMapping
    public ResponseEntity<?> guardarEmpleado(@Valid@RequestBody Empleados empleados) {
       try{
            Optional<Empleados> nuevoEmpleado = empleadosService.guardarEmpleado(empleados);
        
            // Validación: Si el service devolvió null, el RUN ya estaba registrado
            if(nuevoEmpleado.isPresent()){
                return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEmpleado.get());
            } else{
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El empleado ya existe");
            }
       } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
       }
    }

    // LEER: GET a /api/v1/registro/empleados/{run}
    @GetMapping("/buscar/{runEmpleado}")
    public ResponseEntity<?> buscarPorRun(@PathVariable String runEmpleado) {
       try{
            Optional<Empleados> empleadoEncontrado = empleadosService.buscarPorRun(runEmpleado);

            if (empleadoEncontrado.isPresent()) {
                return ResponseEntity.ok(empleadoEncontrado.get()); // Devuelve 200 OK
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empleado no encontrado"); // Devuelve 404 Not Found
            }
       }catch (RuntimeException e){
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
       }
    }

    // ACTUALIZAR
    @PutMapping("/actualizar/{runEmpleado}")
    public ResponseEntity<?> actualizarEmpleado(@PathVariable String runEmpleado, @RequestBody Empleados empleadosNuevosDatos) {
        try{
            Empleados empleadoActualizado = empleadosService.ActualizarEmpleados(runEmpleado, empleadosNuevosDatos);

            if (empleadoActualizado != null) {
                return ResponseEntity.ok(empleadoActualizado); // Devuelve 200 OK
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se a encontrado al empleado para revizar"); // Devuelve 404 Not Found
            }
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Delete
    @DeleteMapping("/eliminar/{runEmpleado}")
    public ResponseEntity<String> eliminarPorRun(@PathVariable String runEmpleado) {
        try{
            boolean fueEliminado = empleadosService.eliminarEmpleadosRun(runEmpleado);

            if (fueEliminado) {
                return ResponseEntity.ok("Empleado con RUN " + runEmpleado + " fue eliminado correctamente."); // Devuelve 200 OK
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se a logrado encontrar el run para eliminar"); // Devuelve 404 Not Found
            }
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //links para probar en postman
    //1-post-http://localhost:8081/api/v1/registro/empleados
    //2-get-http://localhost:8081/api/v1/registro/empleados/buscar
    //3-put-http://localhost:8081/api/v1/registro/empleados/actualizar
    //4-delete-http://localhost:8081/api/v1/registro/empleados/eliminar
}
