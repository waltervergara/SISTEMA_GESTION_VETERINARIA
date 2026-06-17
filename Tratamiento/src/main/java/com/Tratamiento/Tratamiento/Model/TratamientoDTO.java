package com.Tratamiento.Tratamiento.Model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TratamientoDTO {

    @NotBlank
    private String nombre;

    @NotBlank
    private String diagnostico;
    
    @NotBlank
    private String medicacion; 
    
    @NotBlank
    private String observacion;
    
    @NotNull
    private LocalDateTime fechaRevision;


    @NotBlank
    private MascotaDTO mascota; 

}
