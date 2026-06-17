package com.pagos.facturacion.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Factura")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Factura {
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotEmpty(message = "no puede aver espacion en el codigo")
    @NotBlank(message = "no puede estar en blanco el codigo de factura")
    @Column(length = 255, nullable = false)
    private String codigoFactura;
    
    @NotBlank(message = "los detalles no puede estar vacios")
    @Column(length = 255, nullable = false)
    private String detalles;
    
    @Past(message = "la fecha de la factura no puede ser en el futuro")
    @NotNull(message = "la fecha no puede ser nula")
    @Column(name = "fecha_emision",nullable = false)
    private LocalDateTime fechaEmision;
    
    @Positive(message = "el precio no puede ser negativo o cero")
    @NotNull(message = "el precio no puede estar vacio")
    @Column(nullable = false)
    private BigDecimal precio;

    


    // run del propietario
    @NotBlank(message = "el run de propietario no puede estar en blanco") 
    @Column(length =  13, nullable = false)
    private String runPropietario;

}
