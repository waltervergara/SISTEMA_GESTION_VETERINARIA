package com.example.Identificacion.Dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EmpleadoResponse {
    private String runEmpleado;
    private String nombre;
    private LocalDate fecha_nacimiento;
    private String cargo;
    private String gmail;
    private String numero_telefono;
}