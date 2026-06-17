package Historial.HistorialMascota.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import Historial.HistorialMascota.Modelo.Historial;
import Historial.HistorialMascota.Modelo.HistorialDTO;
import Historial.HistorialMascota.Service.HistorialService;
import java.util.Optional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/historiales")
public class HistorialController {

    @Autowired
    private HistorialService historialService;

    // 1. POST: Guardar un historial nuevo por primera vez
    @PostMapping("/guardar")
    public ResponseEntity<?> guardarHistorial(@Valid @RequestBody Historial historial) {
        try {
            // Usamos el método que creaste que devuelve Optional<Historial>
            Optional<Historial> nuevoHistorial = historialService.guardarHistorial(historial);
            
            if (nuevoHistorial.isPresent()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(nuevoHistorial.get());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("No se pudo registrar el historial.");
            }
        } catch (RuntimeException e) {
            // Captura tu excepción personalizada ("El historial ya existe")
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar el historial: " + e.getMessage());
        }
    }

    // 2. GET: Obtener el historial completo (Con las citas del otro microservicio)
    @GetMapping("/detalle/{codigoMicrochip}")
    public ResponseEntity<?> obtenerHistorialCompleto(@PathVariable String codigoMicrochip) {
        try {
            // Llama a tu método con .map() que devuelve el Optional<HistorialDTO>
            Optional<HistorialDTO> historialCompleto = historialService.obtenerHistorialCompleto(codigoMicrochip);

            if (historialCompleto.isPresent()) {
                return ResponseEntity.ok(historialCompleto.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontró un historial registrado para el microchip: " + codigoMicrochip);
            }
        } catch (Exception e) {
            // Si el Feign Client falla o algo se rompe, cae aquí
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener el historial de la mascota: " + e.getMessage());
        }
    }
    
    // (Opcional) Puedes agregar aquí tus métodos de PUT (Actualizar) y DELETE (Eliminar)
    // siguiendo la misma estructura que hiciste en CitaMedicaController.
}
