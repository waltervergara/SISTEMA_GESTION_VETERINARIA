package Microservicio.Registro.Modelo;

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
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Mascota")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mascota {
    
    @NotBlank(message = "no puede estar vacio ek codigoMicrochip")
    @Id
    @Column(length = 15 ,nullable = true)
    private String codigoMicrochip;


    @NotBlank(message = "el nombre no puede estar en blanco")
    @Column( nullable = false)
    private String nombre;
    
    @PositiveOrZero(message = "la edad no puede ser negativa")//@PositiveOrZero , esto hace que el numero no pueda ser negativo
    @NotNull(message = "la edad no puede ser null")
    @Column( nullable = false)
    private Integer edad;
    
    @PositiveOrZero(message = "el año no puede ser negativo")
    @NotNull(message = "el año no puede ser null")
    @Column(nullable = false)
    private Integer año_nacimiento;
    
    @NotBlank(message = "la especie no puede estar en blanco")
    @Column( nullable = false)
    private String especie;

    @NotBlank(message = "la raza no puede estar en blanco")
    @Column( nullable = false)
    private String raza;
   

    //Aqui viene lo medio enredado que es la vinculacion de datos con la tabla de dueño
    
    @NotNull(message = "no puede tener un propietario vacio")
    @ManyToOne//la relacion , pueden existir muchas mascotas para un solo dueño
    @JoinColumn(name = "run_propietario" ,//los datos que se van a unir a la tabla
    foreignKey = @ForeignKey(name = "fk_mascota_propietario"))//como se llamara esa tabla
    private Propietario propietario;


}
