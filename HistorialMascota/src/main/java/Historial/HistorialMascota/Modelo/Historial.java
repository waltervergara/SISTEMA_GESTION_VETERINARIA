package Historial.HistorialMascota.Modelo;

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
    private Long id_historial;

    // Guardamos el codigo del animal al que pertenece este historial
    @NotBlank(message = "no puede estar vacio el codigo microchip de la mascota")
    @Column(nullable = false, unique = true)
    private String codigoMicrochip;
    
    @PastOrPresent(message = "la fecha de reacion no puede ser del futuro")
    @NotNull(message = "la fecha de creacion no puede ser null")
    @Column(nullable = false)
    private LocalDateTime fechaCreacionHistorial;
    
    @NotBlank(message = "la observacion general no puede estar vacia")
    @Column(nullable = false, length = 1000)
    private String observacionesGenerales;

    
}
