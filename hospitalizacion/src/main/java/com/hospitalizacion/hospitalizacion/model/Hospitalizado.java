package com.hospitalizacion.hospitalizacion.model;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "hospitalizados")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Modelo que representa un animal hospitalizado en el sistema veterinario")
public class Hospitalizado {
    
    @NotEmpty(message = "no puede haber espacios en blanco en el codigo de hospitalizacion")
    @NotBlank(message = "no puede estar vacio el codigo de hospitalizacion")
    @Id
    @Column(length = 15 ,nullable = false )
    @Schema(description = "Código único de hospitalización", example = "HOSP-2024-001")
    private String codigoHospitalizacion;
    
    @NotBlank(message = "no puede estar en blanco la sala")
    @Column( length = 100, nullable = false)
    @Schema(description = "Sala donde está hospitalizado el animal", example = "Sala UCI Veterinaria")
    private String sala;
    
    @NotNull(message = "la hora de monitorio no puede ser null")
    @Column( nullable = false)
    @Schema(description = "Fecha y hora del último monitoreo del animal", example = "2024-06-15T10:30:00")
    private LocalDateTime horaMonitoreo;
    
    @NotBlank(message = "la descripcion no puede estar en blanco")
    @Column(length = 255 ,nullable = false)
    @Schema(description = "Descripción del estado y condición del animal hospitalizado", example = "Animal estable, en observación post operatoria")
    private String descripcion;
    
    @NotBlank(message = "el codigoMicrochip no puede estar vacio")
    @Column(length = 15 ,nullable = false)
    @Schema(description = "Codigo identificador de la mascota" , example = "9851210123456")
    private String codigoMicrochip; 

}
