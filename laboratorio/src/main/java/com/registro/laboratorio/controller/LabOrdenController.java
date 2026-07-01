package com.registro.laboratorio.controller;

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
@Tag(name = "Laboratorio", description = "Gestión de órdenes de laboratorio veterinario")
public class LabOrdenController {
    
    @Autowired
    private LabOrdenService labOrdenService;

    // 1. Guardar una nueva cita en la base de datos local
    //DOCUMENTACION SWAGGER UI
    @Operation(
            summary = "Guardar una nueva orden de laboratorio",
            description = "Registra una nueva orden de laboratorio en el sistema. El nombre de la orden debe ser único."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden de laboratorio creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LabOrden.class))),
            @ApiResponse(responseCode = "400", description = "Ya existe una orden con ese código",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Ya existe una orden de laboratorio registrada con ese código de consulta."))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error al guardar la orden de laboratorio: detalle del error")))
    })
    @PostMapping
    public ResponseEntity<?> guardarLabOrden(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de la orden de laboratorio a registrar",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{\"nombreOrden\":\"LAB-2024-001\",\"fechaPedido\":\"2024-06-15T10:30:00\",\"tipoExamen\":\"Hemograma completo\",\"estado\":\"Pendiente\",\"descripcion\":\"Examen de sangre para control rutinario\",\"codigoMicrochip\":\"MCH-123456789\"}"
                    )
            )
    )@Valid @RequestBody LabOrden labOrden) {
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
    //DOCUMENTACION SWAGGER UI
    @Operation(
            summary = "Obtener detalle completo de una orden de laboratorio",
            description = "Busca una orden de laboratorio por su nombre y retorna los datos completos incluyendo información de la mascota, propietario y empleado obtenidos desde otros microservicios."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden de laboratorio encontrada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LabOrdenDTO.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró la orden de laboratorio",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "No se encontró ninguna orden de laboratorio con el código: LAB-2024-001"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor o fallo en microservicios",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error al obtener los detalles de la orden de laboratorio: detalle del error")))
    })
    @GetMapping("/detalle/{nombre}")
    public ResponseEntity<?> obtenerDetalleLabOrden(@Parameter(
            name = "nombre",
            description = "Nombre único de la orden de laboratorio a consultar",
            example = "LAB-2024-001",
            required = true
    )@PathVariable String nombre) {
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

    //Actualizar una orden de laboratorio
    //DOCUMENTACION SWAGGER UI
    @Operation(
            summary = "Actualizar una orden de laboratorio existente",
            description = "Actualiza los datos de una orden de laboratorio existente usando su nombre como identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden de laboratorio actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LabOrden.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró la orden de laboratorio a actualizar",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error: No se encontró el nombre de la orden laboratorio para actualizar."))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error interno al actualizar la orden de laboratorio")))
    })
    @PutMapping("/actualizar/{nombre}")
    public ResponseEntity<?> actualizarLabOrden(@Parameter(
            name = "nombre",
            description = "Nombre único de la orden de laboratorio a actualizar",
            example = "LAB-2024-001",
            required = true
    )@PathVariable String nombre,@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos actualizados de la orden de laboratorio",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{\"fechaPedido\":\"2024-06-20T10:30:00\",\"tipoExamen\":\"Hemograma completo y química sanguínea\",\"estado\":\"En proceso\",\"descripcion\":\"Examen de seguimiento\",\"codigoMicrochip\":\"MCH-123456789\"}"
                    )
            )
    ) @RequestBody LabOrden datos) {
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
