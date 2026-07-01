package com.Tratamiento.Tratamiento.Controller;

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

import com.Tratamiento.Tratamiento.Model.*;
import com.Tratamiento.Tratamiento.Service.*;


import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/v1/tratamiento")
@Tag(name = "Tratamiento", description = "Gestión de tratamientos médicos veterinarios")
public class TratamientoController {
    
    @Autowired
    private TratamientoService tratamientoService;

    // 1. Guardar una nuevo tratamiento
    //DOCUMENTACION SWAGGER
    @Operation(
            summary = "Guardar un nuevo tratamiento",
            description = "Registra un nuevo tratamiento médico en el sistema. El nombre debe ser único."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tratamiento creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Tratamiento.class))),
            @ApiResponse(responseCode = "400", description = "Ya existe un tratamiento con ese código",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Ya existe un tratamiento registrado con ese código de consulta."))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error al guardar el tratamiento: detalle del error")))
    })
    @PostMapping
    public ResponseEntity<?> guardarTratamiento(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del tratamiento a registrar",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{\"nombre\":\"Orden-001\",\"diagnostico\":\"Infección bacteriana leve\",\"medicacion\":\"Amoxicilina 500mg cada 8 horas\",\"observacion\":\"Reposo por 3 días\",\"fechaRevision\":\"2024-06-15T10:30:00\",\"codigoMicrochip\":\"MCH-123456789\"}"
                    )
            )
    )@Valid @RequestBody Tratamiento tratamiento) {
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

    //Buscar tratamiento (con mascota)
    @Operation(
            summary = "Obtener detalle completo de un tratamiento",
            description = "Busca un tratamiento por su nombre y retorna los datos completos incluyendo la información de la mascota obtenida desde el microservicio de Registro."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tratamiento encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TratamientoDTO.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró el tratamiento",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "No se encontró ningún tratamiento con el código: Orden-001"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error al obtener los detalles del tratamiento: detalle del error")))
    })
    @GetMapping("/detalle/{nombre}")
    public ResponseEntity<?> obtenerDetalleTratamiento(@Parameter(
            name = "nombre",
            description = "Nombre único del tratamiento a consultar",
            example = "Orden-001",
            required = true
    )@PathVariable String nombre) {
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

    //Actualizar tratamiento
    @Operation(
            summary = "Actualizar un tratamiento existente",
            description = "Actualiza los datos de un tratamiento existente usando su nombre como identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tratamiento actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Tratamiento.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró el tratamiento a actualizar",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error: No se encontró el nombre del tratamiento para actualizar."))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error interno al actualizar el tratamiento")))
    })
    @PutMapping("/actualizar/{nombre}")
    public ResponseEntity<?> actualizarLabOrden( @Parameter(
            name = "nombre",
            description = "Nombre único del tratamiento a actualizar",
            example = "Orden-001",
            required = true
    )@PathVariable String nombre,@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos actualizados del tratamiento",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{\"diagnostico\":\"Infección bacteriana moderada\",\"medicacion\":\"Amoxicilina 1g cada 12 horas\",\"observacion\":\"Reposo por 5 días\",\"fechaRevision\":\"2024-06-20T10:30:00\",\"codigoMicrochip\":\"MCH-123456789\"}"
                    )
            )
    ) @RequestBody Tratamiento datos) {
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
