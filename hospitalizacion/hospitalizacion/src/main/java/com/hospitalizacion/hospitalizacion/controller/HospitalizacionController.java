package com.hospitalizacion.hospitalizacion.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospitalizacion.hospitalizacion.model.*;
import com.hospitalizacion.hospitalizacion.service.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/hospitalizacion")
public class HospitalizacionController {
    @Autowired
    private HospitalizadoService hospitalizadoService;

    // 1. Guardar una nueva cita en la base de datos local
    @PostMapping("/guardar")
    public ResponseEntity<?> guardarHospitalizacion(@Valid@RequestBody Hospitalizado hospitalizado) {
        try {
            Optional<Hospitalizado> nuevaHospitalizacion = hospitalizadoService.guardarHospitalizacion(hospitalizado);
            
            if (nuevaHospitalizacion.isPresent()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(nuevaHospitalizacion.get());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Ya existe una Hospitalizacion registrada con ese código de consulta.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar la Hospitalizacion: " + e.getMessage());
        }
    }

    // 2. Buscar la cita completa (con mascota, propietario y empleado desde los otros microservicios)
    @GetMapping("/detalle/{codigoHospitalizacion}")
    public ResponseEntity<?> obtenerDetalleHospitalizacion(@PathVariable String codigoHospitalizacion) {
        try {
            Optional<HospitalizadoDTO> hospitalizacionCompleta = hospitalizadoService.obtenerDetalleCompletoHospitalizacion(codigoHospitalizacion);

            if (hospitalizacionCompleta.isPresent()) {
                return ResponseEntity.ok(hospitalizacionCompleta.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontró ninguna Hospitalizacion con el código: " + codigoHospitalizacion);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener los detalles de la Hospitalizacion: " + e.getMessage());
        }
    }
    
    //links de prueba para postamn
    //1-post-http://localhost:8086/api/v1/hospitalizacion/guardar
    //2-get-http://localhost:8086/api/v1/hospitalizacion/detalle/

}
