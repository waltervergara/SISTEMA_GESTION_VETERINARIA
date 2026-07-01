package com.Tratamiento.Tratamiento.Model;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Tratamiento")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Modelo que representa una orden de laboratorio en el sistema")
public class Tratamiento {
    
    @NotBlank(message = "el nombre no puede estar en blanco")
    @Id
    @Column(nullable = false)
    @Schema(description = "Nombre identificador de la orden", example = "Tratamiento-001")
    private String nombre;
    
    @NotBlank(message = "el diagnostico no puede estar en blanco")
    @Column(nullable = false)
    @Schema(description = "Diagnóstico médico del paciente", example = "Infección bacteriana leve")
    private String diagnostico;
    
    @NotBlank(message = "la medicina no puede estar en blanco")
    @Column(nullable = false)
    @Schema(description = "Medicación recetada al paciente", example = "Amoxicilina 500mg cada 8 horas")
    private String medicacion; 
    
    @NotBlank(message = "la observacion no puede estar en blanco")
    @Column(nullable = false)
    @Schema(description = "Observaciones adicionales del veterinario", example = "Reposo por 3 días, evitar ejercicio")
    private String observacion;
    
    @NotNull(message = "la fecha de revision no puede ser null")
    @Column(nullable = false)
    @Schema(description = "Fecha y hora de la revisión médica", example = "2026 -06-15T10:30:00")
    private LocalDateTime fechaRevision;

    
    @NotBlank(message = "el codigoMicrochip no puede estar en blanco")
    @Column(nullable = false)
    @Schema(description = "Codigo identificador de la mascota" , example = "9851210123456")
    private String codigoMicrochip; 



}
