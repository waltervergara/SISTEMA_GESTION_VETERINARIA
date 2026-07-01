package com.registro.laboratorio.controller;

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

import com.registro.laboratorio.model.LabOrden;
import com.registro.laboratorio.model.LabOrdenDTO;
import com.registro.laboratorio.service.LabOrdenService;

import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/v1/laboratorio")
public class LabOrdenController {
    
    @Autowired
    private LabOrdenService labOrdenService;

    // 1. Guardar una nueva cita en la base de datos local
    @PostMapping
    public ResponseEntity<?> guardarLabOrden(@Valid @RequestBody LabOrden labOrden) {
        try {
            Optional<LabOrden> nuevaLabOrden = labOrdenService.guardarLabOrden(labOrden);
            
            if (nuevaLabOrden.isPresent()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(nuevaLabOrden.get());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Ya existe una orden de laboratorio registrada con ese código de consulta.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar la orden de laboratorio: " + e.getMessage());
        }
    }

    // 2. Buscar la cita completa (con mascota, propietario y empleado desde los otros microservicios)
    @GetMapping("/detalle/{nombre}")
    public ResponseEntity<?> obtenerDetalleLabOrden(@PathVariable String nombre) {
        try {
            Optional<LabOrdenDTO> LabOrdenCompleta = labOrdenService.obtenerDetalleCompletoLabOrden(nombre);

            if (LabOrdenCompleta.isPresent()) {
                return ResponseEntity.ok(LabOrdenCompleta.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontró ninguna orden de laboratorio con el código: " + LabOrdenCompleta);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener los detalles de la orden de laboratorio: " + e.getMessage());
        }
    }

    @PutMapping("/actualizar/{nombre}")
    public ResponseEntity<?> actualizarLabOrden(@PathVariable String nombre, @RequestBody LabOrden datos) {
        try {
            LabOrden actualizada = labOrdenService.ActualizarLabOrde(nombre, datos);
            
            if (actualizada != null) {
                // Sello 200 (OK): Actualización exitosa
                return ResponseEntity.ok(actualizada);
            } else {
                // Sello 404 (NOT FOUND): No encontró el nombre en la base de datos
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: No se encontró el nombre de la orden laboratorio para actualizar.");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //links de prueba en postman
    //1-post-http://localhost:8087/api/v1/laboratorio
    //2-get-http://localhost:8087/api/v1/laboratorio/detalle/
    //3-put-http://localhost:8087/api/v1/laboratorio/actualizar/

}
