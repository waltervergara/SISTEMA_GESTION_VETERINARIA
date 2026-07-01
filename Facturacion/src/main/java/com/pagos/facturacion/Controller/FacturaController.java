package com.pagos.facturacion.Controller;

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

import com.pagos.facturacion.Service.FacturaService;
import com.pagos.facturacion.Model.Factura;
import com.pagos.facturacion.Model.FacturaDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/factura")
@Tag(name = "Factura", description = "Gestión de facturas del sistema de pagos")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    // 1. Guardar una nueva factura en la base de datos local
    //DOCUMENTACION SWAGERR UI
    @Operation(
            summary = "Guardar una nueva factura",
            description = "Registra una nueva factura en el sistema. El código de factura debe ser único."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Factura creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Factura.class))),
            @ApiResponse(responseCode = "400", description = "Ya existe una factura con ese código",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Ya existe una factura registrada con ese código de consulta."))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error al guardar la factura: detalle del error")))
    })
    @PostMapping("/guardar")
    public ResponseEntity<?> guardarLabOrden( @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de la factura a registrar",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{\"codigoFactura\":\"FAC-2024-001\",\"detalles\":\"Consulta veterinaria y vacunación\",\"fechaEmision\":\"2024-06-15T10:30:00\",\"precio\":25990.00,\"runPropietario\":\"12.345.678-9\"}"
                    )
            )
    )@Valid @RequestBody Factura factura) {
        try {
            Optional<Factura> nuevaFactura = facturaService.guardarFactura(factura);
            
            if (nuevaFactura.isPresent()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(nuevaFactura.get());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Ya existe una factura registrada con ese código de consulta.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar la factura: " + e.getMessage());
        }
    }

    // 2. Buscar la factura completa ( propietario microservicio)
    //DOCUMENTACION SWAGERR UI
    @Operation(
            summary = "Obtener detalle completo de una factura",
            description = "Busca una factura por su código y retorna los datos completos incluyendo la información del propietario obtenida desde el microservicio de Registro."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Factura encontrada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FacturaDTO.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró la factura",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "No se encontró ninguna factura con el código: FAC-2024-001"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error al obtener los detalles de la factura: detalle del error")))
    })
    @GetMapping("/detalle/{codigoFactura}")
    public ResponseEntity<?> obtenerDetalleLabOrden(@Parameter(
            name = "codigoFactura",
            description = "Código único de la factura a consultar",
            example = "FAC-2024-001",
            required = true
    )@PathVariable String codigoFactura) {
        try {
            Optional<FacturaDTO> facturaCompleta = facturaService.obtenerDetalleCompletoFactura(codigoFactura);

            if (facturaCompleta.isPresent()) {
                return ResponseEntity.ok(facturaCompleta.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontró ninguna factura con el código: " + codigoFactura);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener los detalles de la factura: " + e.getMessage());
        }
    }

}


//http://localhost:8088/api/v1/factura/guardar
//http://localhost:8088/api/v1/factura/detalle/
