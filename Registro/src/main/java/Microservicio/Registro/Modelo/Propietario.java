package Microservicio.Registro.Modelo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

//Agregar la importación de HATEOAS
import org.springframework.hateoas.RepresentationModel;

@Entity //Entity tratara a esta clase como un tabla de base de datos
@Table(name = "Propietario")//nombre de la tabla
@Data
@AllArgsConstructor
@NoArgsConstructor
//Agregamos esta anotación para evitar conflictos de memoria entre Lombok y HATEOAS
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Modelo que representa a un propietario en el sistema")
//Añadir el "extends" a la declaración de la clase
public class Propietario extends RepresentationModel<Propietario> {
    
    @NotBlank(message = "el run no puede estar en black")
    @Id
    @Column(length = 13 , nullable = false)//Column , es una columna de la tabla , unique es para un dato unico en la tabla 
    @Schema(description = "Identificador unico del propietario y Rut del propietario" , example = "12.345.678-9")
    private String runPropietario; //length es para el rango de escritura
    
    @NotBlank(message = "el nombre no puede estar en blanco")
    @Column(nullable = false)
    @Schema(description = "Nombre del propietario" , example = "Alonso")
    private String nombre;
    
    @NotBlank(message = "el apellido no puede estar en blanco")
    @Column( nullable = false)
    @Schema(description = "Apellido del propietario" , example = "Contreras")
    private String apellido;
    
    @NotBlank(message = "el correo no puede estar en blanco")
    @Email(message = "tiene que ser un formato Email")
    @Column( nullable = false)
    @Schema(description = "Correo del propietario",example = "goku@gmail.com")
    private String correo;
    

    @NotBlank(message = "el numero de telefono no puede estar en blanco")
    @Column(length = 12 , nullable = false)
    @Schema(description = "Telefono del propietario",example = "+56954203889")
    private String telefono;

}
