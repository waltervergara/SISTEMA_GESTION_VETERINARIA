package Historial.HistorialMascota.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import Historial.HistorialMascota.Client.CitaClient;
import Historial.HistorialMascota.Client.MascotaClient;
import Historial.HistorialMascota.Modelo.CitaDTO;
import Historial.HistorialMascota.Modelo.Historial;
import Historial.HistorialMascota.Modelo.HistorialDTO;
import Historial.HistorialMascota.Repository.HistorialRepository;
import feign.FeignException;

@Service
public class HistorialService {

    @Autowired
    private HistorialRepository historialRepository;

    @Autowired
    private CitaClient citaClient;

    @Autowired
    private MascotaClient mascotaClient;

    
    //post
    public Optional<Historial> guardarHistorial(Historial historial){
        try{
            mascotaClient.obtenerMascotaporCodigo(historial.getCodigoMicrochip());
        } catch (FeignException.NotFound e) {
            // Si el microservicio de Mascotas devuelve un 404, bloqueamos el guardado
            throw new RuntimeException("Error: No se puede crear el historial porque la mascota con microchip " + historial.getCodigoMicrochip() + " no existe.");
        } catch (Exception e) {
            // Por si el microservicio de mascotas está apagado
            throw new RuntimeException("Error de comunicación con el servicio de mascotas: " + e.getMessage());
        }
        
        try{
        Optional<Historial> existe = historialRepository.findByCodigoMicrochip(historial.getCodigoMicrochip());

            if (existe.isPresent()) {
                throw new RuntimeException("El historial con el codigo " + historial.getCodigoMicrochip() + " ya existe ");
            }
        
            return Optional.of(historialRepository.save(historial));
        }catch(DataAccessException e){
            System.err.println("Error en la base de datos al guardar el historial : " + e.getMessage());
            throw new RuntimeException("Error interno al guardar en la base de datos");
        }
        
    }
    

    //Get
    public Optional<HistorialDTO> obtenerHistorialCompleto(String codigoMicrochip) {
        
        // Buscamos en la BD (devuelve Optional<Historial>)
        return historialRepository.findByCodigoMicrochip(codigoMicrochip).map(historial -> {
            // Si el historial existe, la función .map() ejecuta este bloque de código:
                    
            // 1. Traemos las citas externas
            List<CitaDTO> listaCitasExternas = citaClient.obtenerCitasPorMicrochip(codigoMicrochip);
                    
            // 2. Construimos el DTO
            HistorialDTO historialDTO = new HistorialDTO();
            historialDTO.setId_historial(historial.getId_historial());
            historialDTO.setCodigoMicrochip(historial.getCodigoMicrochip());
            historialDTO.setFechaCreacionHistorial(historial.getFechaCreacionHistorial());
            historialDTO.setObservacionesGenerales(historial.getObservacionesGenerales());
            historialDTO.setCitas(listaCitasExternas);
                    
            return historialDTO; // Retorna el DTO envuelto automáticamente en un Optional
        }); 
                
    }
}
