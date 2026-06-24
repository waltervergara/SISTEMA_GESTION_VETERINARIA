package com.registro.empleados.model;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

// IMPORTACIÓN DE HATEOAS AÑADIDA
import org.springframework.hateoas.RepresentationModel;

@Entity
@Table(name="empleados")
@Data
@AllArgsConstructor
@NoArgsConstructor
// ANOTACIÓN AÑADIDA PARA EVITAR CONFLICTOS LOMBOK/HATEOAS
@EqualsAndHashCode(callSuper = false)
public class Empleados extends RepresentationModel<Empleados> { // HIEREDANDO DE REPRESENTATIONMODEL
    
    @NotEmpty(message = "no puede aver espacios en el run")
    @NotBlank(message = "el run no puede estar vacio")
    @Id
    @Column(length = 13 , nullable = false)
    @Schema(description = "Identificador unico del empleado", example = "98.765.432-1")
    private String runEmpleado;
    
    @NotBlank(message = "tiene que poner un nombre no puede estar vacio")
    @Column(nullable = false)
    @Schema(description = "Nombre del empleado", example = "Walter Vergara")
    private String nombre;
    
    @NotNull(message = "la fecha no puede estar vacia")
    @Past(message = "tiene que ser una fecha en pasado")
    @Column(nullable = false)
    @Schema(description = "Fecha de nacimiento del empleado", example = "2009-10-01")
    private LocalDate fecha_nacimiento;
    
    @NotBlank(message = "el cargo no puede estar vacio")
    @Column(nullable = false)
    @Schema(description = "Cargo o rol del empleado en la veterinaria", example = "Secretario")
    private String cargo;
    
    @Email(message = "Tiene que ser un formato Email (NombreCorreo@gmail.com)")
    @Column(nullable = false)
    @Schema(description = "Correo del empleado" , example = "walter@gmail.com")
    private String gmail;
    
    @NotBlank(message = "tiene que ser un numero de telefono, no puede estar vacio")
    @Column(length = 12 , nullable = false)
    @Schema(description = "Numeto telefonico del empleado" , example = "+56912345678")
    private String numero_telefono;
}