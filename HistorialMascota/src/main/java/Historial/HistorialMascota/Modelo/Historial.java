package Historial.HistorialMascota.Modelo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "historiales")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Historial {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del historial generado automáticamente", example = "1")
    private Long id_historial;

    // Guardamos el codigo del animal al que pertenece este historial
    @NotBlank(message = "no puede estar vacio el codigo microchip de la mascota")
    @Column(nullable = false, unique = true)
    @Schema(description = "Codigo identificador de la mascota" , example = "9851210123456")
    private String codigoMicrochip;
    
    @PastOrPresent(message = "la fecha de reacion no puede ser del futuro")
    @NotNull(message = "la fecha de creacion no puede ser null")
    @Column(nullable = false)
    @Schema(description = "Fecha y hora de creación del historial, no puede ser en el futuro", example = "2024-06-15T10:30:00")
    private LocalDateTime fechaCreacionHistorial;
    
    @NotBlank(message = "la observacion general no puede estar vacia")
    @Column(nullable = false, length = 1000)
    @Schema(description = "Observaciones generales del veterinario sobre el estado de salud de la mascota", example = "Mascota en buen estado general, vacunas al día, próxima revisión en 6 meses")
    private String observacionesGenerales;

    
}