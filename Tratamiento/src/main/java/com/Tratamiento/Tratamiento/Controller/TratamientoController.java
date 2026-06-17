package com.Tratamiento.Tratamiento.Controller;

import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Tratamiento.Tratamiento.Model.*;
import com.Tratamiento.Tratamiento.Service.*;


import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/v1/tratamiento")
public class TratamientoController {
    
    @Autowired
    private TratamientoService tratamientoService;

    // 1. Guardar una nuevo tratamiento
    @PostMapping
    public ResponseEntity<?> guardarTratamiento(@Valid @RequestBody Tratamiento tratamiento) {
        try {
            Optional<Tratamiento> nuevoTratamiento = tratamientoService.guardarTratamiento(tratamiento);
            
            if (nuevoTratamiento.isPresent()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTratamiento.get());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Ya existe un tratamiento registrada con ese código de consulta.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar el tratamiento: " + e.getMessage());
        }
    }

    // 2. Buscar tratamiento (con mascota)
    @GetMapping("/detalle/{nombre}")
    public ResponseEntity<?> obtenerDetalleTratamiento(@PathVariable String nombre) {
        try {
            Optional<TratamientoDTO> tratamientoCompleta = tratamientoService.obtenerDetalleCompleto(nombre);

            if (tratamientoCompleta.isPresent()) {
                return ResponseEntity.ok(tratamientoCompleta.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontró ninguno tratamiento con el código: " + tratamientoCompleta);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener los detalles de el tratamiento: " + e.getMessage());
        }
    }

    @PutMapping("/actualizar/{nombre}")
    public ResponseEntity<?> actualizarLabOrden(@PathVariable String nombre, @RequestBody Tratamiento datos) {
        try {
            Tratamiento actualizada = tratamientoService.actualizarTratamiento(nombre, datos);
            
            if (actualizada != null) {
                // Sello 200 (OK): Actualización exitosa
                return ResponseEntity.ok(actualizada);
            } else {
                // Sello 404 (NOT FOUND): No encontró el nombre en la base de datos
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: No se encontró el nombre de el tratamiento para actualizar.");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //links de prueba en postman
    //1-post-http://localhost:8089/api/v1/tratamiento
    //2-get- http://localhost:8089/api/v1/tratamiento/detalle/
    //3-put-http://localhost:8089/api/v1/tratamiento/actualizar/

}
