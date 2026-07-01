package Historial.HistorialMascota.Modelo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List; // Importante para la lista

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistorialDTO {

    //Datos propios del historial (idénticos a tu Entidad Historial)

    @NotNull
    @Schema(description = "Identificador único del historial generado automáticamente", example = "1")
    private Long id_historial;

    @NotBlank
    @Schema(description = "Codigo identificador de la mascota" , example = "9851210123456")
    private String codigoMicrochip;

    @NotNull
    @Schema(description = "Fecha y hora de creación del historial, no puede ser en el futuro", example = "2024-06-15T10:30:00")
    private LocalDateTime fechaCreacionHistorial;

    @NotBlank
    @Schema(description = "Observaciones generales del veterinario sobre el estado de salud de la mascota", example = "Mascota en buen estado general, vacunas al día, próxima revisión en 6 meses")
    private String observacionesGenerales;

    // 2. Aquí es donde ocurre la magia: La lista de todas las citas traídas del otro microservicio
    @NotNull
    @Schema(description = "Trae todas las citas que a tenido la mascota")
    private List<CitaDTO> citas; 
}