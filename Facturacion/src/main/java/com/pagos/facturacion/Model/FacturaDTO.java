package com.pagos.facturacion.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para la creación y transferencia de datos de una factura")
public class FacturaDTO {

    @NotEmpty
    @NotBlank
    @Schema(description = "Código único de la factura", example = "FAC-2024-001")
    private String codigoFactura;

    @NotBlank
    @Schema(description = "Descripción detallada de los servicios o productos facturados", example = "Consulta veterinaria y vacunación")
    private String detalles;


    @NotNull
    @Schema(description = "Fecha y hora de emisión de la factura", example = "2024-06-15T10:30:00")
    private LocalDateTime fechaEmision;


    @NotNull
    @Schema(description = "Monto total de la factura, debe ser mayor a cero", example = "25990.00")
    private BigDecimal precio;

    @Valid
    @NotNull
    @Schema(description = "Datos del propietario asociado a la factura")
    private PropietarioDTO propietario;

}
