package com.inventario.inventario.model;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Modelo que representa un producto en el inventario veterinario")
public class Inventario {
    
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Schema(description = "Identificador único del producto generado automáticamente", example = "1")
    private Long id;
    
    @NotBlank(message = "el nombre no puede estar en blanco")
    @Column(unique = true,length = 255,nullable =  false)
    @Schema(description = "Nombre único del producto en inventario", example = "Amoxicilina 500mg")
    private String nombre;
    
    @NotNull(message = "la fecha de elaboracion no puede ser null")
    @Column(nullable =  false)
    @Schema(description = "Fecha de elaboración del producto", example = "2024-01-15")
    private LocalDate fecha_elaboracion;
    
    @NotBlank(message = "el vencimiento no puede estar en blanco")
    @Column(length = 100,nullable =  false)
    @Schema(description = "Fecha de vencimiento del producto", example = "2026-01-15")
    private String vencimiento;
    
    @PositiveOrZero(message = "el Stock no puede ser negativo")
    @NotNull(message = "el stock no puede ser null")
    @Column(nullable =  false)
    @Schema(description = "Cantidad disponible en stock, no puede ser negativo", example = "150")
    private Long stock;
    
    @NotBlank(message = "la descripcion no puede estar en blanco")
    @Column(length = 255, nullable =  false)
    @Schema(description = "Descripción detallada del producto", example = "Antibiótico de amplio espectro para uso veterinario")
    private String descripcion;
    
    @Positive(message = "el precio no puede ser negativo o cero")
    @NotNull(message = "el precio no puede ser null")
    @Column(nullable = false)
    @Schema(description = "Precio del producto, debe ser mayor a cero", example = "5990")
    private Long precio;

}
