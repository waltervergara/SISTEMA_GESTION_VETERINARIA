package com.Tratamiento.Tratamiento.Model;

import java.time.LocalDateTime;

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
public class Tratamiento {
    
    @NotBlank(message = "el nombre no puede estar en blanco")
    @Id
    @Column(nullable = false)
    private String nombre;
    
    @NotBlank(message = "el diagnostico no puede estar en blanco")
    @Column(nullable = false)
    private String diagnostico;
    
    @NotBlank(message = "la medicina no puede estar en blanco")
    @Column(nullable = false)
    private String medicacion; 
    
    @NotBlank(message = "la observacion no puede estar en blanco")
    @Column(nullable = false)
    private String observacion;
    
    @NotNull(message = "la fecha de revision no puede ser null")
    @Column(nullable = false)
    private LocalDateTime fechaRevision;

    
    @NotBlank(message = "el codigoMicrochip no puede estar en blanco")
    @Column(nullable = false)
    private String codigoMicrochip; 



}
