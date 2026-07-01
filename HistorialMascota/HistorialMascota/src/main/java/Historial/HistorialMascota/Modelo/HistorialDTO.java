package Historial.HistorialMascota.Modelo;

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

    
    private Long id_historial;

    @NotBlank
    private String codigoMicrochip;

    @NotNull
    private LocalDateTime fechaCreacionHistorial;

    @NotBlank
    private String observacionesGenerales;

    // 2. Aquí es donde ocurre la magia: La lista de todas las citas traídas del otro microservicio
    @NotNull
    private List<CitaDTO> citas; 
}