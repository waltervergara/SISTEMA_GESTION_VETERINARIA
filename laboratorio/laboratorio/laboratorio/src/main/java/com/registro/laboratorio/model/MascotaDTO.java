package com.registro.laboratorio.model;

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
    private String codigoMicrochip;

    @NotBlank
    private String nombre;

    @NotNull
    private Integer edad;

    @NotBlank
    private String especie;

    @NotBlank
    private String raza;

}
