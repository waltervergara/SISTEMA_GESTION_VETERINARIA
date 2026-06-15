package com.example.Identificacion.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpleadoRequest {
    // Campos obligatorios que exige el MS de Empleados
    private String runEmpleado;
    private String nombre;
    private LocalDate fecha_nacimiento;
    private String cargo;
    private String gmail;
    private String numero_telefono;
}