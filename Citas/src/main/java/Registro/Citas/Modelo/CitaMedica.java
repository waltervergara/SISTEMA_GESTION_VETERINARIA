package Registro.Citas.Modelo;

import java.time.LocalDateTime;

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
public class CitaMedica {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El codigo de la consulta no puede estar vacio")
    @Column(unique = true , nullable = false ) 
    private String codigoConsulta;

    @Future(message = "La fecha de la cita medica debe ser en ell futuro")
    @NotNull(message = "La fecha no puede estar vacia")
    @Column(nullable = false)
    private LocalDateTime fechaHora;//al ser una cita es para manejar fecha y hora de la cita

    @NotBlank(message = "El motivo no puede estar vacio")
    @Column(nullable = false)
    private String motivo;

    @NotBlank(message = "El estado no puede estar vacio")
    @Column(nullable = false)
    private String estado;

    @NotBlank(message = "La observacion no puede estar vacia")
    @Column(length = 500 , nullable = true)
    private String observaciones;

    @NotBlank(message = "El codigo del microchip no puede estar vacio")
    @Column(nullable = false)
    private String codigoMicrochip; 

    @NotBlank(message = "El Run del propietario no puede estar vacio")
    @Column(nullable = false)
    private String runPropietario; 

    @NotBlank(message = "El Run del empleado no puede estar vacio")
    @Column(nullable = false)
    private String runEmpleado; 
}
