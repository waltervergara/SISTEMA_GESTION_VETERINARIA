package com.hospitalizacion.hospitalizacion.controller;

import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Hospitalización", description = "Gestión de animales hospitalizados en el sistema veterinario")
public class HospitalizacionController {
    @Autowired
    private HospitalizadoService hospitalizadoService;

    // 1. Guardar una nueva cita en la base de datos local
    //DOCUMENTACION SWAGGER UI
    @Operation(
            summary = "Guardar una nueva hospitalización",
            description = "Registra una nueva hospitalización en el sistema. El código de hospitalización debe ser único."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Hospitalización creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Hospitalizado.class))),
            @ApiResponse(responseCode = "400", description = "Ya existe una hospitalización con ese código",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Ya existe una Hospitalizacion registrada con ese código de consulta."))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error al guardar la Hospitalizacion: detalle del error")))
    })
    @PostMapping("/guardar")
    public ResponseEntity<?> guardarHospitalizacion(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de la hospitalización a registrar",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{\"codigoHospitalizacion\":\"HOSP-001\",\"sala\":\"Sala UCI Veterinaria\",\"horaMonitoreo\":\"2024-06-15T10:30:00\",\"descripcion\":\"Animal estable, en observación post operatoria\",\"codigoMicrochip\":\"9851210123456\"}"
                    )
            )
    )@Valid@RequestBody Hospitalizado hospitalizado) {
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
    //DOCUMENTACION SWAGGER UI
    @Operation(
            summary = "Obtener detalle completo de una hospitalización",
            description = "Busca una hospitalización por su código y retorna los datos completos incluyendo información de la mascota, propietario y empleado obtenidos desde otros microservicios."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hospitalización encontrada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HospitalizadoDTO.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró la hospitalización",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "No se encontró ninguna Hospitalizacion con el código: HOSP-2024-001"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor o fallo en microservicios",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error al obtener los detalles de la Hospitalizacion: detalle del error")))
    })
    @GetMapping("/detalle/{codigoHospitalizacion}")
    public ResponseEntity<?> obtenerDetalleHospitalizacion(@Parameter(
            name = "codigoHospitalizacion",
            description = "Código único de la hospitalización a consultar",
            example = "HOSP-001",
            required = true
    )@PathVariable String codigoHospitalizacion) {
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
