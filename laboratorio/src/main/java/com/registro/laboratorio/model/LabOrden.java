package com.registro.laboratorio.model;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Modelo que representa una orden de laboratorio en el sistema")
public class LabOrden {
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la orden generado automáticamente", example = "1")
    private Long id;
    
    @NotBlank(message = "el nombre de la orden no puede estar vacia")
    @Column(unique = true, nullable = false)
    @Schema(description = "Nombre único de la orden de laboratorio", example = "LAB-2024-001")
    private String nombreOrden;
    
    @NotNull(message = "la fecha de pedido no puede ser null")
    @Column(nullable = false)
    @Schema(description = "Fecha y hora en que se realizó el pedido de la orden", example = "2024-06-15T10:30:00")
    private LocalDateTime fechaPedido;
    
    @NotBlank(message = "el tipo de exameno no puede estar en blanco")
    @Column(length =  255,nullable = false)
    @Schema(description = "Tipo de examen solicitado en la orden", example = "Hemograma completo")
    private String tipoExamen;
    
    @NotBlank(message = "el estado no puede estar en blanco")
    @Column(length =  255,nullable = false)
    @Schema(description = "Estado actual de la orden de laboratorio", example = "Pendiente")
    private String estado;
    
    @NotBlank(message = "la descripcion no puede esatr en blanco")
    @Column(length =  255, nullable =  false)
    @Schema(description = "Descripción detallada de la orden de laboratorio", example = "Examen de sangre para control rutinario")
    private String descripcion;
    
    @NotBlank(message = "el codigoMicrochip no puede estar en blanco")
    @Column(nullable = false)
    @Schema(description = "Codigo identificador de la mascota" , example = "9851210123456")
    private String codigoMicrochip; 





}
