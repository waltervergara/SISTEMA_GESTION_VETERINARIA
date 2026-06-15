package Registro.Citas.Controller;

import Registro.Citas.Modelo.CitaMedicaDTO;
import Registro.Citas.Modelo.CitaMedica;
import Registro.Citas.Service.CitaMedicaService;
import jakarta.validation.Valid;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/citas")
public class CitaMedicaController {

    @Autowired
    private CitaMedicaService citaMedicaService;

    //Guardar una nueva cita en la base de datos local
    @PostMapping("/guardar")
    public ResponseEntity<?> guardarCita(@Valid @RequestBody CitaMedica citaMedica) {
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
    @GetMapping("/detalle/{codigoConsulta}")
    public ResponseEntity<?> obtenerDetalleCita(@PathVariable String codigoConsulta) {
        try {
            Optional<CitaMedicaDTO> citaCompleta = citaMedicaService.obtenerDetalleCompletoCita(codigoConsulta);

            if (citaCompleta.isPresent()) {
                return ResponseEntity.ok(citaCompleta.get());
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
    @PutMapping("/actualizar/{codigoConsulta}")
    public ResponseEntity<?> actualizarCita(@PathVariable String codigoConsulta , @RequestBody CitaMedica citaMedicaNueva){
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

    @DeleteMapping("/eliminar/{codigoConsulta}")
    public ResponseEntity<String> eliminarPorCodigoConsulta(@PathVariable String codigoConsulta){
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
    @GetMapping("/mascota/{codigoMicrochip}")
    public ResponseEntity<?> obtenerCitasPorMicrochip(@PathVariable String codigoMicrochip) {
        try {
            // Llamamos al nuevo método que acabamos de crear
            List<CitaMedicaDTO> listaCitas = citaMedicaService.obtenerCitasPorMicrochip(codigoMicrochip);

            if (!listaCitas.isEmpty()) {
                return ResponseEntity.ok(listaCitas);
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); 
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener las citas: " + e.getMessage());
    }
}

    //links de prueba

    //post-http://localhost:8082/api/v1/citas/guardar
    //get-http://localhost:8082/api/v1/citas/detalle/
    //put-http://localhost:8082/api/v1/citas/actualizar/
    //delete-http://localhost:8082/api/v1/citas/eliminar/
}