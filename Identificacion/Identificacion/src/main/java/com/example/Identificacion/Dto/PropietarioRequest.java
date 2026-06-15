package com.example.Identificacion.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropietarioRequest {
    private String runPropietario;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
}