package Microservicio.Registro.Controller;

import java.util.Optional;

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
public class PropietarioController {

    @Autowired
    private PropietarioService propietarioService;

    //GUARDAR
    @PostMapping()
    public ResponseEntity<?> guardarPropietario(@Valid@RequestBody Propietario propietario) {
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

    //BUSCAR POR RUN
    @GetMapping("/buscar/{runPropietario}")
    public ResponseEntity<?> buscarPorRun(@PathVariable String runPropietario) {
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

    //ACTUALIZAR
    @PutMapping("/actualizar/{runPropietario}")
    public ResponseEntity<?> actualizarPropietario(@PathVariable String runPropietario, @RequestBody Propietario datos) {
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

    //ELIMINAR
    @DeleteMapping("/eliminar/{runPropietario}")
    public ResponseEntity<?> eliminarPropietario(@PathVariable String runPropietario) {
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

    //Links para probar en postamn
    //1-post-http://localhost:8080/api/v1/registro/propietarios
    //2-get-run-http://localhost:8080/api/v1/registro/propietarios/buscar/
    //3-put-run-http://localhost:8080/api/v1/registro/propietarios/actualizar/
    //4-delete-run-http://localhost:8080/api/v1/registro/propietarios/eliminar/
}