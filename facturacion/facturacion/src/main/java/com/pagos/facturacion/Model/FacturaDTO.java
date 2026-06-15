package com.pagos.facturacion.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
public class FacturaDTO {

    @NotEmpty
    @NotBlank
    private String codigoFactura;
    
    @NotBlank
    private String detalles;

    
    @NotNull
    private LocalDateTime fechaEmision;

    
    @NotNull
    private BigDecimal precio;

    @Valid
    @NotNull
    private PropietarioDTO propietario;

}
