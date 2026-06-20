package Microservicio.Registro.Controller;

import java.util.Optional;

import Microservicio.Registro.Modelo.Propietario;
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

import Microservicio.Registro.Modelo.Mascota;
import Microservicio.Registro.Service.MascotaService;
import jakarta.validation.Valid;

//@CrossOrigin(origins = "*") // Listo para HTML y Postman
@RestController
@RequestMapping("/api/v1/registro/mascotas") // Todas las URLs de este archivo empezarán con /mascotas
@Tag(name = "Mascota" , description = "Gestion de las mascotas")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;


    //===========GUARDAR UNA NUEVA MASCOTA=======
    //DOCUMENTACION SWAGERR UI
    @Operation(summary = "Guardar una nueva mascota",
            description = "Registra una nueva mascota en el sistema junto a su propietario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201" , description = "La mascota se a registrado con exito",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Mascota.class))),
            @ApiResponse(responseCode = "409" , description = "La mascota ya existe",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "La masctoa ya existe"))),
            @ApiResponse(responseCode = "400" , description = "Url mal escrita",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error la ruta o link que intentas consultar no existe"))),
            @ApiResponse(responseCode = "500" , description = "Error interno en el servidor",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error interno al guardar en la base de datos"))),
    })
     @PostMapping()
    public ResponseEntity<?> guardarMascota(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(
                    value = "{\"codigoMicrochip\":\"9851210123456\",\"nombre\":\"Firulais\",\"edad\":3,\"año_nacimiento\":2023,\"especie\":\"Perro\",\"raza\":\"Pastor Alemán\",\"propietario\":{\"runPropietario\":\"12.345.678-9\"}}"
            ))
    )@Valid@RequestBody Mascota mascota) {
        try {
            Optional<Mascota> resultado = mascotaService.GuardarMascota(mascota);
            
            if (resultado.isPresent()) {
                // Sello 201 (CREATED): Todo salió perfecto
                return ResponseEntity.status(HttpStatus.CREATED).body(resultado.get());
            } else {
                // Sello 409 (CONFLICT): El microchip ya existía
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: El código de microchip ya está registrado.");
            }
        } catch (RuntimeException e) {
            // Sello 500 (INTERNAL SERVER ERROR): Falló el servidor o la BD
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

  
    //BUSCAR MASCOTA POR CHIP
    //DOCUMENTACION SWAGERR UI
    @Operation(summary = "Revisar datos de una mascota",
            description = "Busca y retorna la información de una mascota según su código de microchip")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description = "Se encuentra la mascota",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Mascota.class))),
            @ApiResponse(responseCode = "404" , description = "No se encuentra la mascota",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "No se a encontrado la mascota"))),
            @ApiResponse(responseCode = "400" , description = "Url mal escrita",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string" , example = "Error la ruta o link que intentas consultar no existe"))),
            @ApiResponse(responseCode = "500" , description = "Error con el servidor/base de datos",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string" , example = "Error con el servidor")))
    })
   @GetMapping("/buscar/{codigoMicrochip}")
    public ResponseEntity<?> buscarPorChip(@Parameter(name = "codigoMicrochip" , description = "Codigo de la mascota a consultar",example = "9851210123456", required = true)@PathVariable String codigoMicrochip) {
        try {
            Optional<Mascota> mascota = mascotaService.buscarPorChip(codigoMicrochip);
            
            if (mascota.isPresent()) {
                // Sello 200 (OK): Encontró la mascota
                return ResponseEntity.ok(mascota.get());
            } else {
                // Sello 404 (NOT FOUND): No existe ese chip
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Mascota no encontrada con ese chip.");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    //ACTUALIZAR DATOS DE UNA MASCOTA
    //DOCUMENTACION SWAGERR UI
    @Operation(summary = "Editar datos de una mascota",
            description = "Actualiza los datos de una mascota existente usando su código de microchip")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description = "Mascota actualizada con exito",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Mascota.class))),
            @ApiResponse(responseCode = "404" , description = "No se encuentra la mascota",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "No se a encontrado la mascota"))),
            @ApiResponse(responseCode = "400" , description = "Url mal escrita",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string" , example = "Error la ruta o link que intentas consultar no existe"))),
            @ApiResponse(responseCode = "500" , description = "Error con el servidor/base de datos",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string" , example = "Error con el servidor")))
    })
    @PutMapping("/actualizar/{codigoMicrochip}")
    public ResponseEntity<?> actualizarMascota(@Parameter(
            name = "codigoMicrochip",
            description = "Código de microchip de la mascota a editar",
            example = "9851210123456",
            required = true
    )@PathVariable String codigoMicrochip,@io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(
                    value = "{\"nombre\":\"Firulais\",\"edad\":3,\"año_nacimiento\":2023,\"especie\":\"Perro\",\"raza\":\"Pastor Alemán\",\"propietario\":{\"runPropietario\":\"12.345.678-9\"}}"
            ))
    ) @RequestBody Mascota datos) {
        try {
            Mascota actualizada = mascotaService.ActualizarMascota(codigoMicrochip, datos);
            
            if (actualizada != null) {
                // Sello 200 (OK): Actualización exitosa
                return ResponseEntity.ok(actualizada);
            } else {
                // Sello 404 (NOT FOUND): No encontró el ID en la base de datos
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: No se encontró el ID de la mascota para actualizar.");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    
    //ELIMINAR UNA MASCOTA POR CHIP
    //DOCUMENTACION SWAGERR UI
    @Operation(summary = "Eliminar a una mascota de la base de datos",
            description = "Elimina permanentemente el registro de una mascota según su código de microchip")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description = "Se elimina la mascota correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string" , example = "Se a eliminado correctamente la mascota"))),
            @ApiResponse(responseCode = "404" , description = "No se encuentra la mascota a eliminar",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "No se a encontrado la mascota"))),
            @ApiResponse(responseCode = "400" , description = "Url mal escrita",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string" , example = "Error la ruta o link que intentas consultar no existe"))),
            @ApiResponse(responseCode = "500" , description = "Error con el servidor/base de datos",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string" , example = "Error con el servidor")))
    })
  @DeleteMapping("/eliminar/{codigoMicrochip}")
    public ResponseEntity<?> eliminarMascota(@Parameter(
            name = "codigoMicrochip",
            description = "Código de microchip de la mascota a eliminar",
            example = "9851210123456",
            required = true
    )@PathVariable String codigoMicrochip) {
        try {
            boolean eliminado = mascotaService.eliminarMascotaExistente(codigoMicrochip);
            
            if (eliminado) {
                // Sello 200 (OK): Borrado exitoso
                return ResponseEntity.ok("Éxito: Mascota eliminada correctamente.");
            } else {
                // Sello 404 (NOT FOUND): No existía ese chip para borrar
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: No se encontró el chip para eliminar.");
            }
        } catch (RuntimeException e) {
            // Sello 400 (BAD REQUEST): Si la BD bloquea el borrado por alguna razón
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //links para probar
    //1-post-http://localhost:8080/api/v1/registro/mascotas
    //2-get-chip-http://localhost:8080/api/v1/registro/mascotas/buscar/
    //3-put-chip-http://localhost:8080/api/v1/registro/mascotas/actualizar/
    //4-delete-chip-http://localhost:8080/api/v1/registro/mascotas/eliminar/


    //Link para documentacion Swagger UI
    //http://localhost:8080/swagger-ui.html

    //====EJEMPLO EN FORMATO JASON====
    //{
    //"codigoMicrochip": "9851210123456",
    //"nombre": "Firulais",
    //"edad": 3,
    //"año_nacimiento": 2023,
    //"especie": "Perro",
    //"raza": "Pastor Alemán",
    //"propietario": {
    //"runPropietario": "4"
    //}
    //}
}