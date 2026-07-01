package Historial.HistorialMascota.Modelo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpleadosDTO {
    @NotBlank
    @Schema(description = "Nombre del empleado", example = "Walter Vergara")
    private String nombre;

    @NotBlank
    @Schema(description = "Cargo o rol del empleado en la veterinaria", example = "Secretario")
    private String cargo;
}