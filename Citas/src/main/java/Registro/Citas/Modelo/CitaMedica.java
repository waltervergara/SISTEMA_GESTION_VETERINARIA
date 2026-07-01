package Registro.Citas.Modelo;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Esto va a base de datos

@Entity
@Table(name = "citas_medicas")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Entidad que representa una Cita Médica en el sistema veterinario")
public class CitaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID autogenerado de la cita", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "El codigo de la consulta no puede estar vacio")
    @Column(unique = true , nullable = false )
    @Schema(description = "Código único identificador de la consulta", example = "CITA-2026-001")
    private String codigoConsulta;

    @Future(message = "La fecha de la cita medica debe ser en el futuro")
    @NotNull(message = "La fecha no puede estar vacia")
    @Column(nullable = false)
    @Schema(description = "Fecha y hora programada para la cita (debe ser futura)", example = "2026-12-01T15:30:00")
    private LocalDateTime fechaHora;

    @NotBlank(message = "El motivo no puede estar vacio")
    @Column(nullable = false)
    @Schema(description = "Razón principal de la visita médica", example = "Vacunación anual y chequeo general")
    private String motivo;

    @NotBlank(message = "El estado no puede estar vacio")
    @Column(nullable = false)
    @Schema(description = "Estado actual de la cita", example = "PROGRAMADA")
    private String estado;

    @NotBlank(message = "La observacion no puede estar vacia")
    @Column(length = 500 , nullable = true)
    @Schema(description = "Notas adicionales sobre la cita o el paciente", example = "El paciente es alérgico a la penicilina")
    private String observaciones;

    @NotBlank(message = "El codigo del microchip no puede estar vacio")
    @Column(nullable = false)
    @Schema(description = "Código del microchip de la mascota paciente", example = "981020000394857")
    private String codigoMicrochip;

    @NotBlank(message = "El Run del propietario no puede estar vacio")
    @Column(nullable = false)
    @Schema(description = "RUT/RUN del dueño de la mascota", example = "12345678-9")
    private String runPropietario;

    @NotBlank(message = "El Run del empleado no puede estar vacio")
    @Column(nullable = false)
    @Schema(description = "RUT/RUN del veterinario o empleado que atiende", example = "9876543-2")
    private String runEmpleado;
}
