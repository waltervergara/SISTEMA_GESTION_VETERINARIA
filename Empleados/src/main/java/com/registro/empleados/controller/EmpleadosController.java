package com.registro.empleados.controller;

import com.registro.empleados.model.Empleados; // O Empleado si lo cambiaste a singular
import com.registro.empleados.service.EmpleadosService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/registro/empleados")
@Tag(name = "Empleados", description = "Gestion de los empleados de la veterinaria")
public class EmpleadosController {

    @Autowired
    private EmpleadosService empleadosService;

    //======Guardar un nuevo empleado=====
    //DOCUMENTACION SWAGGER UI
    @Operation(
            summary = "Guardar un nuevo empleado",
            description = "Registra un nuevo empleado en el sistema usando su RUN como identificador único. Si el RUN ya existe retorna 409."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "El empleado se ha creado con éxito",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Empleados.class))),
            @ApiResponse(responseCode = "409", description = "El empleado ya existe",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "El empleado ya existe"))),
            @ApiResponse(responseCode = "400", description = "URL mal escrita",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error la ruta o link que intentas consultar no existe"))),
            @ApiResponse(responseCode = "500", description = "Error interno en el servidor",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error interno al guardar en la base de datos")))
    })
    @PostMapping
    public ResponseEntity<?> guardarEmpleado(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(
                    value = "{\"runEmpleado\":\"98.765.432-1\",\"nombre\":\"Walter Vergara\",\"fecha_nacimiento\":\"2009-10-01\",\"cargo\":\"Secretario\",\"gmail\":\"walter@gmail.com\",\"numero_telefono\":\"+56912345678\"}"
            ))
    )@Valid@RequestBody Empleados empleados) {
       try{
            Optional<Empleados> nuevoEmpleado = empleadosService.guardarEmpleado(empleados);

            // Validación: Si el service devolvió null, el RUN ya estaba registrado
            if(nuevoEmpleado.isPresent()){
                return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEmpleado.get());
            } else{
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El empleado ya existe");
            }
       } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
       }
    }

    //========Buscar un empleado=====
    //DOCUMENTACION SWAGGER UI
    @Operation(
            summary = "Buscar empleado por RUN",
            description = "Retorna los datos de un empleado usando su RUN como identificador. Si no existe retorna 404."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Empleados.class))),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Empleado no encontrado"))),
            @ApiResponse(responseCode = "500", description = "Error interno en el servidor",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error interno al consultar la base de datos")))
    })
    @GetMapping("/buscar/{runEmpleado}")
    public ResponseEntity<?> buscarPorRun(
            @Parameter(name ="runEmpleado",description = "RUN del empleado a buscar", example = "98.765.432-1", required = true)
            @PathVariable String runEmpleado) {
       try{
            Optional<Empleados> empleadoEncontrado = empleadosService.buscarPorRun(runEmpleado);

            if (empleadoEncontrado.isPresent()) {
                return ResponseEntity.ok(empleadoEncontrado.get()); // Devuelve 200 OK
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empleado no encontrado"); // Devuelve 404 Not Found
            }
       }catch (RuntimeException e){
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
       }
    }

    // ACTUALIZAR
    @Operation(
            summary = "Actualizar datos de un empleado",
            description = "Actualiza los datos de un empleado existente usando su RUN. Si el empleado no existe retorna 404."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Empleados.class))),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado para actualizar",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "No se a encontrado al empleado para revizar"))),
            @ApiResponse(responseCode = "500", description = "Error interno en el servidor",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error interno al actualizar en la base de datos")))
    })
    @PutMapping("/actualizar/{runEmpleado}")
    public ResponseEntity<?> actualizarEmpleado(
            @Parameter(name ="runEmpleado",description = "RUN del empleado a actualizar", example = "98.765.432-1", required = true)
            @PathVariable String runEmpleado,@io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\"nombre\":\"Walter Vergara\",\"fecha_nacimiento\":\"2009-10-01\",\"cargo\":\"Secretario\",\"gmail\":\"walter@gmail.com\",\"numero_telefono\":\"+56912345678\"}"
                    ))
            )
            @RequestBody Empleados empleadosNuevosDatos) {
        try{
            Empleados empleadoActualizado = empleadosService.ActualizarEmpleados(runEmpleado, empleadosNuevosDatos);

            if (empleadoActualizado != null) {
                return ResponseEntity.ok(empleadoActualizado); // Devuelve 200 OK
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se a encontrado al empleado para revizar"); // Devuelve 404 Not Found
            }
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Delete
    @Operation(
            summary = "Eliminar empleado por RUN",
            description = "Elimina permanentemente un empleado del sistema usando su RUN. Si no se encuentra retorna 404."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado eliminado exitosamente",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Empleado con RUN 98.765.432-1 fue eliminado correctamente."))),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado para eliminar",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "No se a logrado encontrar el run para eliminar"))),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error al procesar la solicitud de eliminación")))
    })
    @DeleteMapping("/eliminar/{runEmpleado}")
    public ResponseEntity<String> eliminarPorRun(
            @Parameter(name = "runEmpleado",description = "RUN del empleado a eliminar", example = "98.765.432-1", required = true)
            @PathVariable String runEmpleado) {
        try{
            boolean fueEliminado = empleadosService.eliminarEmpleadosRun(runEmpleado);

            if (fueEliminado) {
                return ResponseEntity.ok("Empleado con RUN " + runEmpleado + " fue eliminado correctamente."); // Devuelve 200 OK
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se a logrado encontrar el run para eliminar"); // Devuelve 404 Not Found
            }
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //links para probar en postman
    //1-post-http://localhost:8081/api/v1/registro/empleados
    //2-get-http://localhost:8081/api/v1/registro/empleados/buscar
    //3-put-http://localhost:8081/api/v1/registro/empleados/actualizar
    //4-delete-http://localhost:8081/api/v1/registro/empleados/eliminar

    //link Documentacion Swagger UI
    //http://localhost:8081/swagger-ui.html
}
