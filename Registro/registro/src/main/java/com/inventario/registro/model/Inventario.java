package com.inventario.registro.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    
    @Column(unique = true,length = 255,nullable =  false)
    private String nombre;
    
    @Column(nullable =  false)
    private LocalDate fecha_elaboracion;
    
    @Column(length = 100,nullable =  false)
    private String vencimiento;
    
    @Column(nullable =  false)
    private long stock;
    
    @Column(length = 255, nullable =  false)
    private String descripcion;

}
