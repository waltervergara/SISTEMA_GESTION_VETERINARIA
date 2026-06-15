package Historial.HistorialMascota.Modelo;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data


public class CitaDTO {

    //Información propia de la Cita
    //Puedes poner los campos uno a uno o simplemente la entidad CitaMedica
    @NotBlank
    private String codigoConsulta;
    @NotNull
    private LocalDateTime fechaHora;
    @NotBlank
    private String motivo;
    @NotBlank
    private String estado; // Pendiente, Completada, Cancelada
    @NotBlank
    private String observaciones;

    //El detalle de los otros microservicios (Los DTO que ya creamos)
    
    @NotNull
    private PropietarioDTO propietario;
     
    @NotNull
    private MascotaDTO mascota;
    
    @NotNull
    private EmpleadosDTO empleado;
}
