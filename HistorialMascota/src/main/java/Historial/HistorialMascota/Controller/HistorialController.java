package Historial.HistorialMascota.Controller;

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

import org.springframework.web.bind.annotation.*;

import Historial.HistorialMascota.Modelo.Historial;
import Historial.HistorialMascota.Modelo.HistorialDTO;
import Historial.HistorialMascota.Service.HistorialService;
import java.util.Optional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/historiales")
@Tag(name = "Historial", description = "Gestión de historiales médicos de mascotas")
public class HistorialController {

    @Autowired
    private HistorialService historialService;

    // 1. POST: Guardar un historial nuevo por primera vez
    //DOCUMENTACION SWAGGER
    @Operation(
            summary = "Guardar un nuevo historial médico",
            description = "Registra un nuevo historial médico para una mascota. El código de microchip debe ser único, no puede existir más de un historial por mascota."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Historial creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Historial.class))),
            @ApiResponse(responseCode = "400", description = "No se pudo registrar el historial o ya existe uno para esa mascota",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "El historial ya existe para este microchip."))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error al guardar el historial: detalle del error")))
    })
    @PostMapping("/guardar")
    public ResponseEntity<?> guardarHistorial(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del historial médico a registrar",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{\"codigoMicrochip\":\"MCH-123456789\",\"fechaCreacionHistorial\":\"2024-06-15T10:30:00\",\"observacionesGenerales\":\"Mascota en buen estado general, vacunas al día\"}"
                    )
            )
    ) @Valid @RequestBody Historial historial) {
        try {
            // Usamos el método que creaste que devuelve Optional<Historial>
            Optional<Historial> nuevoHistorial = historialService.guardarHistorial(historial);
            
            if (nuevoHistorial.isPresent()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(nuevoHistorial.get());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("No se pudo registrar el historial.");
            }
        } catch (RuntimeException e) {
            // Captura tu excepción personalizada ("El historial ya existe")
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar el historial: " + e.getMessage());
        }
    }

    // 2. GET: Obtener el historial completo (Con las citas del otro microservicio)
    //DOCUMENTACION SWAGGER
    @Operation(
            summary = "Obtener historial completo de una mascota",
            description = "Busca el historial médico por código de microchip y retorna los datos completos incluyendo las citas obtenidas desde el microservicio de Citas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HistorialDTO.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró historial para ese microchip",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "No se encontró un historial registrado para el microchip: MCH-123456789"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor o fallo en microservicio de Citas",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error al obtener el historial de la mascota: detalle del error")))
    })
    @GetMapping("/detalle/{codigoMicrochip}")
    public ResponseEntity<?> obtenerHistorialCompleto(@Parameter(
            name = "codigoMicrochip",
            description = "Código único del microchip de la mascota a consultar",
            example = "MCH-123456789",
            required = true
    )@PathVariable String codigoMicrochip) {
        try {
            // Llama a tu método con .map() que devuelve el Optional<HistorialDTO>
            Optional<HistorialDTO> historialCompleto = historialService.obtenerHistorialCompleto(codigoMicrochip);

            if (historialCompleto.isPresent()) {
                return ResponseEntity.ok(historialCompleto.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontró un historial registrado para el microchip: " + codigoMicrochip);
            }
        } catch (Exception e) {
            // Si el Feign Client falla o algo se rompe, cae aquí
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener el historial de la mascota: " + e.getMessage());
        }
    }
    

}
