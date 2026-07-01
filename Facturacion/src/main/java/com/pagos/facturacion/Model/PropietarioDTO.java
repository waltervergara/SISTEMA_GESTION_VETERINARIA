package com.pagos.facturacion.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropietarioDTO {

    @NotBlank
    @Schema(description = "Identificador unico del propietario y Rut del propietario" , example = "12.345.678-9")
    private String runPropietario;

    @NotBlank
    @Schema(description = "Nombre del propietario" , example = "Alonso")
    private String nombre;

    @NotBlank
    @Schema(description = "Apellido del propietario" , example = "Contreras")
    private String apellido;

    @NotBlank
    @Schema(description = "Telefono del propietario",example = "+56954203889")
    private String telefono;

    @NotBlank
    @Schema(description = "Correo del propietario",example = "goku@gmail.com")
    private String correo;
    
}
