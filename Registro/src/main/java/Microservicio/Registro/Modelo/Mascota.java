package Microservicio.Registro.Modelo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Entity
@Table(name = "Mascota")
@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Modelo que representa la mascota en el sistema")
public class Mascota extends RepresentationModel<Mascota> {
    
    @NotBlank(message = "no puede estar vacio ek codigoMicrochip")
    @Id
    @Column(length = 15 ,nullable = true)
    @Schema(description = "Codigo identificador de la mascota" , example = "9851210123456")
    private String codigoMicrochip;


    @NotBlank(message = "el nombre no puede estar en blanco")
    @Column( nullable = false)
    @Schema(description = "Nombre de la mascota" , example = "Alfredo")
    private String nombre;
    
    @PositiveOrZero(message = "la edad no puede ser negativa")//@PositiveOrZero , esto hace que el numero no pueda ser negativo
    @NotNull(message = "la edad no puede ser null")
    @Column( nullable = false)
    @Schema(description = "Edad de la mascota" , example = "12")
    private Integer edad;
    
    @PositiveOrZero(message = "el año no puede ser negativo")
    @NotNull(message = "el año no puede ser null")
    @Column(nullable = false)
    @Schema(description = "Año de nacimiento" , example = "2009/03/13")
    private Integer año_nacimiento;
    
    @NotBlank(message = "la especie no puede estar en blanco")
    @Column(nullable = false)
    @Schema(description = "Especie del animal" , example = "Perro")
    private String especie;

    @NotBlank(message = "la raza no puede estar en blanco")
    @Column( nullable = false)
    @Schema(description = "Raza del animal" , example = "Bulldog")
    private String raza;
   

    //Aqui viene lo medio enredado que es la vinculacion de datos con la tabla de dueño
    
    @NotNull(message = "no puede tener un propietario vacio")
    @ManyToOne//la relacion , pueden existir muchas mascotas para un solo dueño
    @JoinColumn(name = "run_propietario" ,//los datos que se van a unir a la tabla
    foreignKey = @ForeignKey(name = "fk_mascota_propietario"))//como se llamara esa tabla
    private Propietario propietario;


}
