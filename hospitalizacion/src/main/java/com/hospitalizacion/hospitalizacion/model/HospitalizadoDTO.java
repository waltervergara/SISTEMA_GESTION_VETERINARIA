package com.hospitalizacion.hospitalizacion.model;

import java.time.LocalDateTime;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HospitalizadoDTO {

    @NotBlank
    @Schema(description = "Código único de hospitalización", example = "HOSP-2024-001")
    private String codigoHospitalizacion;

    @NotBlank
    @Schema(description = "Sala donde está hospitalizado el animal", example = "Sala UCI Veterinaria")
    private String sala;

    @NotNull
    @Schema(description = "Fecha y hora del último monitoreo del animal", example = "2024-06-15T10:30:00")
    private LocalDateTime horaMonitoreo;

    @NotBlank
    @Schema(description = "Descripción del estado y condición del animal hospitalizado", example = "Animal estable, en observación post operatoria")
    private String descripcion;
    

    @Valid
    @NotNull
    @Schema(description = "Modelo que representa a la mascota en el sistema")
    private MascotaDTO mascotaDTO; 

}
