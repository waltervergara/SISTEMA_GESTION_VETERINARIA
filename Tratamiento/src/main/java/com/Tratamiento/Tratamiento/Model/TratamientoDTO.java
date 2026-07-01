package com.Tratamiento.Tratamiento.Model;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Nombre identificador de la orden", example = "Tratamiento-001")
    private String nombre;

    @NotBlank
    @Schema(description = "Diagnóstico médico del paciente", example = "Infección bacteriana leve")
    private String diagnostico;
    
    @NotBlank
    @Schema(description = "Medicación recetada al paciente", example = "Amoxicilina 500mg cada 8 horas")
    private String medicacion; 
    
    @NotBlank
    @Schema(description = "Observaciones adicionales del veterinario", example = "Reposo por 3 días, evitar ejercicio")
    private String observacion;
    
    @NotNull
    @Schema(description = "Fecha y hora de la revisión médica", example = "2026 -06-15T10:30:00")
    private LocalDateTime fechaRevision;


    @NotBlank
    @Schema(description = "Modelo de la mascota" )
    private MascotaDTO mascota; 

}
