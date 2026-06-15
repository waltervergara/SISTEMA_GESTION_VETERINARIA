package Microservicio.Registro.Modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity //Entity tratara a esta clase como un tabla de base de datos
@Table(name = "Propietario")//nombre de la tabla
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Propietario {
    
    @NotBlank(message = "el run no puede estar en black")
    @Id
    @Column(length = 13 , nullable = false)//Column , es una columna de la tabla , unique es para un dato unico en la tabla 
    private String runPropietario; //length es para el rango de escritura
    
    @NotBlank(message = "el nombre no puede estar en blanco")
    @Column(nullable = false)
    private String nombre;
    
    @NotBlank(message = "el apellido no puede estar en blanco")
    @Column( nullable = false)
    private String apellido;
    
    @NotBlank(message = "el correo no puede estar en blanco")
    @Email(message = "tiene que ser un formato Email")
    @Column( nullable = false)
    private String correo;
    

    @NotBlank(message = "el numero de telefono no puede estar en blanco")
    @Column(length = 12 , nullable = false)
    private String telefono;

}
