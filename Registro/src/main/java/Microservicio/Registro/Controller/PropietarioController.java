package Microservicio.Registro.Controller;

import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import Microservicio.Registro.Modelo.Propietario;
import Microservicio.Registro.Service.PropietarioService;
import jakarta.validation.Valid;

//@CrossOrigin(origins = "*") // Para evitar problemas de CORS con el frontend
@RestController
@RequestMapping("/api/v1/registro/propietarios")
@Tag(name = "Propietario" , description = "Gestion de los propietarios de mascotas")
public class PropietarioController {

    @Autowired
    private PropietarioService propietarioService;

    //===========GUARDAR=============
    //DOCUMENTACION SWAGERR UI
    @Operation(summary = "Guardar un nuevo propietario",
            description = "Registra un nuevo propietario en el sistema usando su RUN como identificador único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201" , description = "El propietario se a creado con exito",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Propietario.class))),
            @ApiResponse(responseCode = "409" , description = "El propietario ya existe",
                content = @Content(mediaType = "text/plain",
                    schema = @Schema(type = "string", example = "El Propietario ya existe"))),
            @ApiResponse(responseCode = "400" , description = "Url mal escrita",
                content = @Content(mediaType = "text/plain",
                    schema = @Schema(type = "string", example = "Error la ruta o link que intentas consultar no existe"))),
            @ApiResponse(responseCode = "500" , description = "Error interno en el servidor",
                content = @Content(mediaType = "text/plain",
                    schema = @Schema(type = "string", example = "Error interno al guardar en la base de datos"))),
    })
    @PostMapping()
    public ResponseEntity<?> guardarPropietario(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(
                    value = "{\"runPropietario\":\"12.345.678-9\",\"nombre\":\"Alonso\",\"apellido\":\"Contreras\",\"correo\":\"goku@gmail.com\",\"telefono\":\"+56954203889\"}"
            ))
    )@Valid@RequestBody Propietario propietario) {
        try {
            Optional<Propietario> resultado = propietarioService.guardarPropietario(propietario);
            
            if (resultado.isPresent()) {
                //si el optional viene con algo lo guarda
                return ResponseEntity.status(HttpStatus.CREATED).body(resultado.get());
            } else {
                // Si el Optional viene vacío, es porque el RUN ya existe
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El Propietario ya existe");
            }
        } catch (RuntimeException e) {
            // Atrapamos el error técnico que lanzamos en el Service
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //========BUSCAR POR RUN========
    //DOCUMENTACION SWAGERR UI
    @Operation(summary = "Revisar datos de un propietario",
            description = "Busca y retorna la información de un propietario según su RUN en formato chileno")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description = "Se encuentra el propietario",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Propietario.class))),
            @ApiResponse(responseCode = "404" , description = "No se encuentra el propietario",
                content = @Content(mediaType = "text/plain",
                    schema = @Schema(type = "string", example = "No se a encontrado al propietario"))),
            @ApiResponse(responseCode = "400" , description = "Url mal escrita",
                content = @Content(mediaType = "text/plain",
                    schema = @Schema(type = "string" , example = "Error la ruta o link que intentas consultar no existe"))),
            @ApiResponse(responseCode = "500" , description = "Error con el servidor/base de datos",
                content = @Content(mediaType = "text/plain",
                    schema = @Schema(type = "string" , example = "Error con el servidor")))
    })
    @GetMapping("/buscar/{runPropietario}")
    public ResponseEntity<?> buscarPorRun(@Parameter(name = "runPropietario" , description = "Rut del propietario a consultar",example = "12.345.678-9", required = true)@PathVariable String runPropietario) {
        try {
            Optional<Propietario> propietario = propietarioService.buscarPorRun(runPropietario);
            
            if (propietario.isPresent()) {
                //Cuando lo encuentra y muestra la informacion
                return ResponseEntity.ok(propietario.get());
            } else {
                //cuando no encuentra al propietario
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Propietario no encontrado.");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //==========ACTUALIZAR==========
    //DOCUMENTACION SWAGERR UI
    @Operation(summary = "Editar datos de un propietario",
            description = "Actualiza los datos de un propietario existente usando su RUN como identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description = "Propietario Actualizado con exito",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Propietario.class))),
            @ApiResponse(responseCode = "404" , description = "No se encuentra el propietario",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "No se a encontrado al propietario"))),
            @ApiResponse(responseCode = "400" , description = "Url mal escrita",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string" , example = "Error la ruta o link que intentas consultar no existe"))),
            @ApiResponse(responseCode = "500" , description = "Error con el servidor/base de datos",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string" , example = "Error con el servidor")))
    })
    @PutMapping("/actualizar/{runPropietario}")
    public ResponseEntity<?> actualizarPropietario(@Parameter(
            name = "runPropietario",
            description = "Run del propietario a editar los datos",
            example = "12.345.678-9",
            required = true
    )@PathVariable String runPropietario,@io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(
                    value = "{\"nombre\":\"Alonso\",\"apellido\":\"Contreras\",\"correo\":\"goku@gmail.com\",\"telefono\":\"+56954203889\"}"
            ))
    ) @RequestBody Propietario datos) {
        try {
            Propietario actualizado = propietarioService.actualizarPropietario(runPropietario, datos);
            
            if (actualizado != null) {
                //cuando se actualiza con exito
                return ResponseEntity.ok(actualizado);
            } else {
                //no se encuentra el run
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró el propietario para actualizar");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //=========ELIMINAR========
    //DOCUMENTACION SWAGERR UI
    @Operation(summary = "Eliminar a un propietario de la base de datos",
            description = "Elimina permanentemente el registro de un propietario según su RUN. Si tiene mascotas asociadas en la base de datos puede bloquear la operación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description = "Se elimina al propietario correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string" , example = "Se a eliminado correctamente al propietario"))),
            @ApiResponse(responseCode = "404" , description = "No se encuentra el propietario a eliminar",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "No se a encontrado al propietario"))),
            @ApiResponse(responseCode = "400" , description = "Url mal escrita",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string" , example = "Error la ruta o link que intentas consultar no existe"))),
            @ApiResponse(responseCode = "500" , description = "Error con el servidor/base de datos",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string" , example = "Error con el servidor")))
    })
    @DeleteMapping("/eliminar/{runPropietario}")
    public ResponseEntity<?> eliminarPropietario(@Parameter(name = "runPropietario" , description = "Rut del propietario a eliminar" , example = "12.345.678-9", required = true)@PathVariable String runPropietario) {
        try {
            //recibe el true y lo guarda
            boolean eliminado = propietarioService.eliminarPropietarioporRun(runPropietario);
            
            if (eliminado) {
                //si se elimino correctamente
                return ResponseEntity.ok("Propietario eliminado con éxito.");
            } else {
                //si no se encuentra el run
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró el RUN para eliminar.");
            }
        } catch (RuntimeException e) {
            // Por ejemplo, si intentas borrar un dueño que tiene mascotas y la BD lo impide
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    //======LINKS O EJEMPLOS======
    //Links para probar en postman
    //1-post-http://localhost:8080/api/v1/registro/propietarios
    //2-get-run-http://localhost:8080/api/v1/registro/propietarios/buscar/
    //3-put-run-http://localhost:8080/api/v1/registro/propietarios/actualizar/
    //4-delete-run-http://localhost:8080/api/v1/registro/propietarios/eliminar/

    //Link para documentacion Swagger UI
    //http://localhost:8080/swagger-ui.html
}