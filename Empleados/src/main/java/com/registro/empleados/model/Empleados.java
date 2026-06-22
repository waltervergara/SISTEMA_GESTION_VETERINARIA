package com.registro.empleados.model;

import java.time.LocalDate;

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
import lombok.NoArgsConstructor;


@Entity
@Table(name="empleados")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Empleados {
    
    @NotEmpty(message = "no puede aver espacios en el run")
    @NotBlank(message = "el run no puede estar vacio")
    @Id
    @Column(length = 13 , nullable = false)
    private String runEmpleado;
    
    
    @NotBlank(message = "tiene que poner un nombre no puede estar vacio")
    @Column(nullable = false)
    private String nombre;
    
    @NotNull(message = "la fecha no puede estar vacia")
    @Past(message = "tiene que ser una fecha en pasado")
    @Column(nullable = false)
    private LocalDate fecha_nacimiento;
    
    @NotBlank(message = "el cargo no puede estar vacio")
    @Column(nullable = false)
    private String cargo;
    
    @Email(message = "Tiene que ser un formato Email (NombreCorreo@gmail.com)")
    @Column(nullable = false)
    private String gmail;
    
    @NotBlank(message = "tiene que ser un numero de telefono, no puede estar vacio")
    @Column(length = 12 , nullable = false)
    private String numero_telefono;
}
