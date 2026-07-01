package com.registro.laboratorio.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MascotaDTO {

    @NotNull
    @Schema(description = "Codigo identificador de la mascota" , example = "9851210123456")
    private String codigoMicrochip;

    @NotBlank
    @Schema(description = "Nombre de la mascota" , example = "Alfredo")
    private String nombre;

    @NotNull
    @Schema(description = "Edad de la mascota" , example = "12")
    private Integer edad;

    @NotBlank
    @Schema(description = "Especie del animal" , example = "Perro")
    private String especie;

    @NotBlank
    @Schema(description = "Raza del animal" , example = "Bulldog")
    private String raza;

}
