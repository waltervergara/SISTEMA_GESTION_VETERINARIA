package com.registro.laboratorio.model;

import java.time.LocalDateTime;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


//esto sera lo que vera el usuario
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabOrdenDTO {

    @NotBlank
    @Schema(description = "Nombre único de la orden de laboratorio", example = "LAB-2024-001")
    private String nombre;

    @NotNull
    @Schema(description = "Fecha y hora en que se realizó el pedido de la orden", example = "2024-06-15T10:30:00")
    private LocalDateTime fecha_pedido;

    @NotBlank
    @Schema(description = "Tipo de examen solicitado en la orden", example = "Hemograma completo")
    private String tipo_examen;

    @NotBlank
    @Schema(description = "Estado actual de la orden de laboratorio", example = "Pendiente")
    private String estado;
    
    @NotBlank
    @Schema(description = "Descripción detallada de la orden de laboratorio", example = "Examen de sangre para control rutinario")
    private String descripcion;



    //el id del microservicio de registro, especificamente de mascota
    @Valid
    @NotNull
    private MascotaDTO mascotaDTO; 



}
