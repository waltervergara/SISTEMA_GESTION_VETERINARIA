package com.hospitalizacion.hospitalizacion.model;

import java.time.LocalDateTime;

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
public class Hospitalizado {
    
    @NotEmpty(message = "no puede haber espacios en blanco en el codigo de hospitalizacion")
    @NotBlank(message = "no puede estar vacio el codigo de hospitalizacion")
    @Id
    @Column(length = 15 ,nullable = false ) 
    private String codigoHospitalizacion;
    
    @NotBlank(message = "no puede estar en blanco la sala")
    @Column( length = 100, nullable = false)
    private String sala;
    
    @NotNull(message = "la hora de monitorio no puede ser null")
    @Column( nullable = false)
    private LocalDateTime horaMonitoreo;
    
    @NotBlank(message = "la descripcion no puede estar en blanco")
    @Column(length = 255 ,nullable = false)
    private String descripcion;
    
    @NotBlank(message = "el codigoMicrochip no puede estar vacio")
    @Column(length = 15 ,nullable = false)
    private String codigoMicrochip; 

}
