package Registro.Citas.Modelo;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitaMedicaDTO {

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
    @Valid
    @NotNull
    private PropietarioDTO propietario;
    @Valid 
    @NotNull
    private MascotaDTO mascota;
    @Valid
    @NotNull
    private EmpleadosDTO empleado;

}
