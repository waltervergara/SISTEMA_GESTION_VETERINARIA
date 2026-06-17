package com.registro.laboratorio.model;

import java.time.LocalDateTime;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


//esto sera lo que vera el usuario
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabOrdenDTO {

    @NotBlank
    private String nombre;

    @NotNull
    private LocalDateTime fecha_pedido;

    @NotBlank
    private String tipo_examen;

    @NotBlank
    private String estado;
    
    @NotBlank
    private String descripcion;



    //el id del microservicio de registro, especificamente de mascota
    @Valid
    @NotNull
    private MascotaDTO mascotaDTO; 



}
