package com.inventario.registro.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventario")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Inventario {
    
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "el nombre no puede estar en blanco")
    @Column(unique = true,length = 255,nullable =  false)
    private String nombre;
    
    @NotNull(message = "la fecha de elaboracion no puede ser null")
    @Column(nullable =  false)
    private LocalDate fecha_elaboracion;
    
    @NotBlank(message = "el vencimiento no puede estar en blanco")
    @Column(length = 100,nullable =  false)
    private String vencimiento;
    
    @PositiveOrZero(message = "el Stock no puede ser negativo")
    @NotNull(message = "el stock no puede ser null")
    @Column(nullable =  false)
    private Long stock;
    
    @NotBlank(message = "la descripcion no puede estar en blanco")
    @Column(length = 255, nullable =  false)
    private String descripcion;
    
    @Positive(message = "el precio no puede ser negativo o cero")
    @NotNull(message = "el precio no puede ser null")
    @Column(nullable = false)
    private Long precio;

}
