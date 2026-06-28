package com.inventario.inventario.controller;

import com.inventario.inventario.model.Inventario;
import com.inventario.inventario.service.InventarioService;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/registro/inventario")
@Tag(name = "Inventario", description = "Gestión del inventario de productos veterinarios")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;


    // CREAR: POST a /api/v1/registro/empleados
    //DOCUMENTACION SWAGGER UI
    @Operation(
            summary = "Guardar un nuevo producto en inventario",
            description = "Registra un nuevo producto en el inventario. El nombre del producto debe ser único."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Inventario.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe un producto con ese nombre",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "El Inventario ya existe"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error interno al guardar el producto")))
    })
    @PostMapping
    public ResponseEntity<?> guardarInvetario(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del producto a registrar en inventario",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{\"nombre\":\"Amoxicilina 500mg\",\"fecha_elaboracion\":\"2024-01-15\",\"vencimiento\":\"2026-01-15\",\"stock\":150,\"descripcion\":\"Antibiótico de amplio espectro para uso veterinario\",\"precio\":5990}"
                    )
            )
    )@Valid@RequestBody Inventario inventario) {
       try{
            Optional<Inventario> nuevoInventario = inventarioService.guardarInventario(inventario);
        
            // Validación: Si el service devolvió null, el RUN ya estaba registrado
            if(nuevoInventario.isPresent()){
                return ResponseEntity.status(HttpStatus.CREATED).body(nuevoInventario.get());
            } else{
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El Inventario ya existe");
            }
       } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
       }
    }


    //DOCUMENTACION SWAGGER UI
    @Operation(
            summary = "Buscar un producto por nombre",
            description = "Busca y retorna la información de un producto del inventario según su nombre."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Inventario.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró el producto",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Inventario no encontrado"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error interno al buscar el producto")))
    })
    @GetMapping("/{nombre}")
    public ResponseEntity<?> buscarPorNombre(@Parameter(
            name = "nombre",
            description = "Nombre único del producto a consultar",
            example = "Amoxicilina 500mg",
            required = true
    )@PathVariable String nombre) {
       try{
            Optional<Inventario> inventarioEncontrado = inventarioService.buscarPorNombre(nombre);

            if (inventarioEncontrado.isPresent()) {
                return ResponseEntity.ok(inventarioEncontrado.get()); // Devuelve 200 OK
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Inventario no encontrado"); // Devuelve 404 Not Found
            }
       }catch (RuntimeException e){
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
       }
    }

    // ACTUALIZAR: PUT a /api/v1/registro/empleados/{run}
    //DOCUMENTACION SWAGEGR UI
    @Operation(
            summary = "Actualizar un producto del inventario",
            description = "Actualiza los datos de un producto existente en el inventario usando su nombre como identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Inventario.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró el producto a actualizar",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "No se ha encontrado el inventario para revisar"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error interno al actualizar el producto")))
    })
    @PutMapping("/{nombre}")
    public ResponseEntity<?> actualizarInventario(@Parameter(
            name = "nombre",
            description = "Nombre único del producto a actualizar",
            example = "Amoxicilina 500mg",
            required = true
    )@PathVariable String nombre, @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos actualizados del producto",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = "{\"fecha_elaboracion\":\"2024-03-15\",\"vencimiento\":\"2026-03-15\",\"stock\":200,\"descripcion\":\"Antibiótico actualizado\",\"precio\":6500}"
                    )
            )
    ) @RequestBody Inventario nuevoInventario) {
        try{
            Inventario inventarioActualizado = inventarioService.actualizarInventario(nombre, nuevoInventario);

            if (inventarioActualizado != null) {
                return ResponseEntity.ok(inventarioActualizado); // Devuelve 200 OK
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se a encontrado el inventario para revizar"); // Devuelve 404 Not Found
            }
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ELIMINAR: DELETE a /api/v1/registro/empleados/{run}
    //DOCUMENTACION SWAGGER UI
    @Operation(
            summary = "Eliminar un producto del inventario",
            description = "Elimina permanentemente un producto del inventario usando su nombre como identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "inventario con nombre Amoxicilina 500mg fue eliminado correctamente."))),
            @ApiResponse(responseCode = "404", description = "No se encontró el producto a eliminar",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "No se ha logrado encontrar el nombre para eliminar"))),
            @ApiResponse(responseCode = "400", description = "Error al intentar eliminar el producto",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Error al eliminar el producto")))
    })
    @DeleteMapping("/{nombre}")
    public ResponseEntity<String> eliminarPorNombre(@Parameter(
            name = "nombre",
            description = "Nombre único del producto a eliminar",
            example = "Amoxicilina 500mg",
            required = true
    )@PathVariable String nombre) {
        try{
            boolean fueEliminado = inventarioService.eliminarInventarioNombre(nombre);

            if (fueEliminado) {
                return ResponseEntity.ok("inventario con nombre " + nombre + " fue eliminado correctamente."); // Devuelve 200 OK
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se a logrado encontrar el nombre para eliminar"); // Devuelve 404 Not Found
            }
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //http://localhost:8083/api/v1/registro/inventario

}
