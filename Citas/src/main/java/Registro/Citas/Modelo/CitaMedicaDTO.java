package Registro.Citas.Modelo;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

//Se importa la clase de spring HATEOAS.
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
//A notacion para evitar conflictos entre lombok y hateoas.
@EqualsAndHashCode(callSuper = false)
//Se hcae a la clase enteneder el representationalmodel.
public class CitaMedicaDTO extends RepresentationModel<CitaMedicaDTO> {
    
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
