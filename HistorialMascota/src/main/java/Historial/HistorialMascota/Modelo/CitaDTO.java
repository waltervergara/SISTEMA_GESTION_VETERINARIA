package Historial.HistorialMascota.Modelo;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data


public class CitaDTO {

    //Información propia de la Cita
    //Puedes poner los campos uno a uno o simplemente la entidad CitaMedica
    @NotBlank
    @Schema(description = "Código único identificador de la consulta", example = "CITA-2026-001")
    private String codigoConsulta;

    @NotNull
    @Schema(description = "Fecha y hora programada para la cita (debe ser futura)", example = "2026-12-01T15:30:00")
    private LocalDateTime fechaHora;

    @NotBlank
    @Schema(description = "Razón principal de la visita médica", example = "Vacunación anual y chequeo general")
    private String motivo;

    @NotBlank
    @Schema(description = "Estado actual de la cita", example = "PROGRAMADA")
    private String estado; // Pendiente, Completada, Cancelada

    @NotBlank
    @Schema(description = "Notas adicionales sobre la cita o el paciente", example = "El paciente es alérgico a la penicilina")
    private String observaciones;

    //El detalle de los otros microservicios (Los DTO que ya creamos)
    @Valid
    @NotNull
    @Schema(description = "Modelo que representa los datos del propietario")
    private PropietarioDTO propietario;

    @Valid
    @NotNull
    @Schema(description = "Modelo que representa los datos de la mascota")
    private MascotaDTO mascota;

    @Valid
    @NotNull
    @Schema(description = "Modelo que representa los datos del empleado")
    private EmpleadosDTO empleado;
}