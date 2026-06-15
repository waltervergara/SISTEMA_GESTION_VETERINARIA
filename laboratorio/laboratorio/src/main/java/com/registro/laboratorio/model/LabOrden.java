package com.registro.laboratorio.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//esto sera directo para la base de datos
@Entity
@Table(name = "Lab_orden")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabOrden {
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "el nombre de la orden no puede estar vacia")
    @Column(unique = true, nullable = false)
    private String nombreOrden;
    
    @NotNull(message = "la fecha de pedido no puede ser null")
    @Column(nullable = false)
    private LocalDateTime fechaPedido;
    
    @NotBlank(message = "el tipo de exameno no puede estar en blanco")
    @Column(length =  255,nullable = false)
    private String tipoExamen;
    
    @NotBlank(message = "el estado no puede estar en blanco")
    @Column(length =  255,nullable = false)
    private String estado;
    
    @NotBlank(message = "la descripcion no puede esatr en blanco")
    @Column(length =  255, nullable =  false)
    private String descripcion;
    
    @NotBlank(message = "el codigoMicrochip no puede estar en blanco")
    @Column(nullable = false)
    private String codigoMicrochip; 





}
