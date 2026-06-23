package Registro.Citas.Controller;

import Registro.Citas.Modelo.CitaMedicaDTO;
import Registro.Citas.Modelo.CitaMedica;
import Registro.Citas.Service.CitaMedicaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Importaciones obligatorias para HATEOAS
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.Link;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/citas")
@Tag(name = "Cita Medica" , description = "Gestion de Citas Medicas")
public class CitaMedicaController {

    @Autowired
    private CitaMedicaService citaMedicaService;

    //Guardar una nueva cita en la base de datos local
    //DOCUMENTACION SWAGERR UI
    @Operation(summary = "Registra una Cita",
        description = "Registra una cita medica al sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201" , description = "La cita medica se a registrado con exito",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CitaMedica.class))),
            @ApiResponse(responseCode = "409" , description = "La cita ya esta registrada",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "La cita ya esta registrada"))),
            @ApiResponse(responseCode = "400" , description = "Url mal escrita",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error la ruta o link que intentas consultar no existe"))),
            @ApiResponse(responseCode = "500" , description = "Error interno en el servidor",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error interno al guardar en la base de datos"))),
    })
    @PostMapping("/guardar")
     public ResponseEntity<?> guardarCita(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos necesarios para crear una nueva cita médica",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Ejemplo de Cita Médica",
                            value = "{\"codigoConsulta\":\"CITA-2026-001\",\"fechaHora\":\"2026-12-01T15:30:00\",\"motivo\":\"Vacunación anual y chequeo general\",\"estado\":\"PROGRAMADA\",\"observaciones\":\"El paciente es alérgico a la penicilina\",\"codigoMicrochip\":\"981020000394857\",\"runPropietario\":\"12.345.678-9\",\"runEmpleado\":\"9.876.543-2\"}"
                    )
            )
    )@Valid @RequestBody CitaMedica citaMedica) {
        try {
            Optional<CitaMedica> nuevaCita = citaMedicaService.guardarCitaMedica(citaMedica);
            
            if (nuevaCita.isPresent()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCita.get());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Ya existe una cita registrada con ese código de consulta.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar la cita: " + e.getMessage());
        }
    }

    //Buscar la cita completa (con mascota, propietario y empleado desde los otros microservicios)
    //DOCUMENTACION SWAGERR UI
    @Operation(summary = "Obtener los datos de la consulta",
    description = "Obtener los datos de la consulta buscando por su codigo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200" , description = "Se encuentra la cita medica",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = CitaMedica.class))),
        @ApiResponse(responseCode = "404" , description = "No se encuentra la cita medica",
                content = @Content(mediaType = "text/plain",
                        schema = @Schema(type = "string", example = "No se encuentra la cita medica"))),
        @ApiResponse(responseCode = "400" , description = "Url mal escrita",
                content = @Content(mediaType = "text/plain",
                        schema = @Schema(type = "string" , example = "Error la ruta o link que intentas consultar no existe"))),
        @ApiResponse(responseCode = "500" , description = "Error con el servidor/base de datos",
                content = @Content(mediaType = "text/plain",
                        schema = @Schema(type = "string" , example = "Error con el servidor")))
    })
    @GetMapping("/detalle/{codigoConsulta}")
     public ResponseEntity<?> obtenerDetalleCita(@Parameter(
            description = "Código único de la consulta médica que se desea consultar",
            required = true,
            example = "CITA-2026-001"
    )@PathVariable String codigoConsulta) {
        try {
            Optional<CitaMedicaDTO> citaCompleta = citaMedicaService.obtenerDetalleCompletoCita(codigoConsulta);

            if (citaCompleta.isPresent()) {
                CitaMedicaDTO dto = citaCompleta.get();
        
                Link selfLink = linkTo(methodOn(CitaMedicaController.class).obtenerDetalleCita(codigoConsulta)).withSelfRel();
                Link updateLink = linkTo(methodOn(CitaMedicaController.class).actualizarCita(codigoConsulta, null)).withRel("actualizar");
                Link deleteLink = linkTo(methodOn(CitaMedicaController.class).eliminarPorCodigoConsulta(codigoConsulta)).withRel("eliminar");
        
                dto.add(selfLink, updateLink, deleteLink);

                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró ninguna cita con el código: " + codigoConsulta);
    }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener los detalles de la cita: " + e.getMessage());
        }
    }

    //Actualizar
    //DOCUMENTACION SWAGERR UI
    @Operation(summary = "Editar datos la cita medica",
            description = "Actualiza los datos de la cita medica existente usando su codigoContulta como identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description = "Cita medica actualizado con exito",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CitaMedica.class))),
            @ApiResponse(responseCode = "404" , description = "No se encuentra la cita medica",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "No se a encontrado la cita medica"))),
            @ApiResponse(responseCode = "400" , description = "Url mal escrita",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string" , example = "Error la ruta o link que intentas consultar no existe"))),
            @ApiResponse(responseCode = "500" , description = "Error con el servidor/base de datos",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string" , example = "Error con el servidor")))
    })
    @PutMapping("/actualizar/{codigoConsulta}")
    public ResponseEntity<?> actualizarCita(@Parameter(
            description = "Código único de la consulta médica que se desea actualizar",
            required = true,
            example = "CITA-2026-001"
    )@PathVariable String codigoConsulta ,@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "JSON con los nuevos datos para actualizar la cita médica",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Ejemplo de actualización",
                            value = "{\"codigoConsulta\":\"CITA-2026-001\",\"fechaHora\":\"2026-12-01T16:00:00\",\"motivo\":\"Revisión post-operatoria\",\"estado\":\"MODIFICADA\",\"observaciones\":\"La herida sanó perfectamente. Retiro de puntos.\",\"codigoMicrochip\":\"981020000394857\",\"runPropietario\":\"12.345.678-9\",\"runEmpleado\":\"9.876.543-2\"}"
                    )
            )
    ) @RequestBody CitaMedica citaMedicaNueva){
        try{
            CitaMedica citaActualizar = citaMedicaService.actualizarCitaMedica(codigoConsulta, citaMedicaNueva);

            if(citaActualizar != null){
                return ResponseEntity.ok(citaActualizar);
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra la cita");
            }
        }catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //Actualizar
    //DOCUMENTACION SWAGERR UI
    @Operation(summary = "Eliminar una cita medica de la base de datos",
            description = "Elimina permanentemente una cita medica según su codigoConsulta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description = "Se elimina la cita medica correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string" , example = "Se a eliminado correctamente la cita medica"))),
            @ApiResponse(responseCode = "404" , description = "No se encuentra la cita medica",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "No se a encontrado la cita medica"))),
            @ApiResponse(responseCode = "400" , description = "Url mal escrita",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string" , example = "Error la ruta o link que intentas consultar no existe"))),
            @ApiResponse(responseCode = "500" , description = "Error con el servidor/base de datos",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string" , example = "Error con el servidor")))
    })
    @DeleteMapping("/eliminar/{codigoConsulta}")
    public ResponseEntity<String> eliminarPorCodigoConsulta(@Parameter(
            description = "Código único de la consulta médica que se desea eliminar",
            required = true,
            example = "CITA-2026-001"
    )@PathVariable String codigoConsulta){
        try{
            boolean fueEliminado = citaMedicaService.eliminarCitaMedica(codigoConsulta);

            if(fueEliminado){
                return ResponseEntity.ok("Cita medica con el codigo " + codigoConsulta + " fue eliminado correctamente");
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se a logrado encontrar la Cita medica");
            }
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //Get exlusivo para el Historial
     @Operation(
            summary = "Obtener historial de citas por microchip",
            description = "Busca y devuelve todas las citas médicas asociadas al código de microchip de una mascota para armar su historial clínico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Historial de citas encontrado con éxito.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CitaMedicaDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No entontrado la mascota existe pero no registra ninguna cita médica en el historial."
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al procesar la búsqueda."
            )
    })
    @GetMapping("/mascota/{codigoMicrochip}")
    public ResponseEntity<?> obtenerCitasPorMicrochip(@Parameter(
            description = "Código único del microchip de la mascota",
            required = true,
            example = "981020000394857"
    )@PathVariable String codigoMicrochip) {
        try {
            List<CitaMedicaDTO> listaCitas = citaMedicaService.obtenerCitasPorMicrochip(codigoMicrochip);

            if (!listaCitas.isEmpty()) {
                // IMPLEMENTACIÓN DE HATEOAS RECORRIENDO LA LISTA DE CITAS
                for (CitaMedicaDTO dto : listaCitas) {
                    Link selfLink = linkTo(methodOn(CitaMedicaController.class)
                            .obtenerDetalleCita(dto.getCodigoConsulta())).withSelfRel();
                    
                    Link updateLink = linkTo(methodOn(CitaMedicaController.class)
                            .actualizarCita(dto.getCodigoConsulta(), null)).withRel("actualizar");
                    
                    dto.add(selfLink, updateLink);
                }

                return ResponseEntity.ok(listaCitas);
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); 
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener las citas: " + e.getMessage());
        }
    }
}

    //links de prueba

    //post-http://localhost:8082/api/v1/citas/guardar
    //get-http://localhost:8082/api/v1/citas/detalle/
    //put-http://localhost:8082/api/v1/citas/actualizar/
    //delete-http://localhost:8082/api/v1/citas/eliminar/
