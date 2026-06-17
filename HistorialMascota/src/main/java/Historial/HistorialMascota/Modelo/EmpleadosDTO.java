package Historial.HistorialMascota.Modelo;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpleadosDTO {
    @NotBlank
    private String nombre;
    @NotBlank
    private String cargo;

}
