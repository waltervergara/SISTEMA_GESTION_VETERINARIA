package com.hospitalizacion.hospitalizacion.model;

import java.time.LocalDateTime;


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
    private String codigoHospitalizacion;

    @NotBlank
    private String sala;

    @NotNull
    private LocalDateTime hora_monitoreo;

    @NotBlank
    private String descripcion;
    

    @Valid
    @NotNull
    private MascotaDTO mascotaDTO; 

}
