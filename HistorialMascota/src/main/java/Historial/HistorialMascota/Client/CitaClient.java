package Historial.HistorialMascota.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import Historial.HistorialMascota.Modelo.CitaDTO;

// "url" debe apuntar al puerto donde corre tu Microservicio de Citas (ej: 8082)
@FeignClient(name = "Citas")
public interface CitaClient {

    // Este endpoint debe coincidir con el que tengas en el controlador de Citas
    // para buscar por el microchip del animal.
    @GetMapping("/api/v1/citas/mascota/{codigoMicrochip}")
    List<CitaDTO> obtenerCitasPorMicrochip(@PathVariable("codigoMicrochip") String codigoMicrochip);
}