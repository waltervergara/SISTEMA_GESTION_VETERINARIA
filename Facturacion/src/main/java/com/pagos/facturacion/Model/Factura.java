package com.pagos.facturacion.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Factura")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Modelo que representa una factura en el sistema de pagos")
public class Factura {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la factura generado automáticamente", example = "1")
    private Long id;

    @NotEmpty(message = "no puede aver espacion en el codigo")
    @NotBlank(message = "no puede estar en blanco el codigo de factura")
    @Column(length = 255, nullable = false)
    @Schema(description = "Código único de la factura", example = "FAC-2024-001")
    private String codigoFactura;

    @NotBlank(message = "los detalles no puede estar vacios")
    @Column(length = 255, nullable = false)
    @Schema(description = "Descripción detallada de los servicios o productos facturados", example = "Consulta veterinaria y vacunación")
    private String detalles;

    @Past(message = "la fecha de la factura no puede ser en el futuro")
    @NotNull(message = "la fecha no puede ser nula")
    @Column(name = "fecha_emision",nullable = false)
    @Schema(description = "Fecha y hora de emisión de la factura, no puede ser en el futuro", example = "2024-06-15T10:30:00")
    private LocalDateTime fechaEmision;

    @Positive(message = "el precio no puede ser negativo o cero")
    @NotNull(message = "el precio no puede estar vacio")
    @Column(nullable = false)
    @Schema(description = "Monto total de la factura, debe ser mayor a cero", example = "25990.00")
    private BigDecimal precio;




    // run del propietario
    @NotBlank(message = "el run de propietario no puede estar en blanco")
    @Column(length =  13, nullable = false)
    @Schema(description = "RUN del propietario asociado a la factura", example = "12.345.678-9")
    private String runPropietario;

}
