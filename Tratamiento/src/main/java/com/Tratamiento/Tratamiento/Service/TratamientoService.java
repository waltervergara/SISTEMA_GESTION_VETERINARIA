package com.Tratamiento.Tratamiento.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.Tratamiento.Tratamiento.Client.MascotaClient;
import com.Tratamiento.Tratamiento.Model.MascotaDTO;
import com.Tratamiento.Tratamiento.Model.Tratamiento;
import com.Tratamiento.Tratamiento.Model.TratamientoDTO;
import com.Tratamiento.Tratamiento.Repository.TratamientoRepository;

import feign.FeignException;

@Service
public class TratamientoService {

    @Autowired
    private TratamientoRepository tratamientoRepository;
    

    @Autowired 
    private MascotaClient mascotaClient;

    public Optional<Tratamiento> guardarTratamiento(Tratamiento tratamiento) {
    
    
        Optional<Tratamiento> existe = tratamientoRepository.findByNombre(tratamiento.getNombre());
        if(existe.isPresent()){
            // Lanzamos una excepción personalizada en lugar de Optional.empty()
            throw new RuntimeException("Ya existe una Tratamiento con el código: " + tratamiento.getNombre());
        }

        try{
            //llamamos al microservicio de mascota
            mascotaClient.obtenerMascotaporCodigo(tratamiento.getCodigoMicrochip());
        } catch (FeignException.NotFound e){
            throw new RuntimeException("Error: La mascota con el codigo " + tratamiento.getCodigoMicrochip() + " no existe o no se encontrado");
        }

        //Si pasó todas las validaciones, guardamos en la base de datos
        try {
            return Optional.of(tratamientoRepository.save(tratamiento));
        } catch (DataAccessException e) {
            System.err.println("Error de base de datos al intentar registrar el tratamiento: " + e.getMessage());
            throw new RuntimeException("Error interno al guardar en la base de datos");
        } catch(Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            throw new RuntimeException("Error inesperado en el servidor");
        }
    }

    //Buscar por nombre de el tratamiento
    public Optional<Tratamiento> buscarPorNombre(String nombre){
        try{
            return tratamientoRepository.findByNombre(nombre);
        }catch(Exception e){
            System.err.println("Error al buscar el codigo de el tratamiento " + e.getMessage());
            throw new RuntimeException("Error al consultar la base de datos");
        }
    }

    //la mejor forma de definirlo es que , esta parte es la api hacia los otros microservicios , es como un puente de datos seguros
    public Optional<MascotaDTO> obtenerMascota(String codigoMicrochip){
        try {
            MascotaDTO mascota = mascotaClient.obtenerMascotaporCodigo(codigoMicrochip);
            return Optional.ofNullable(mascota);
        } catch (Exception e) {
            System.err.println("Error al obtener datos de la mascota " + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<TratamientoDTO> obtenerDetalleCompleto(String nombre) {
        try {
            //Aqui buscamos la consulta que se guarda premiamente en base de datos
            Optional<Tratamiento> existe = buscarPorNombre(nombre);
            
            //si no la encuentra se detiene y no devuelve nada 
            if (existe.isEmpty()) {
                return Optional.empty(); 
            }
            
            //si la encuentra saca la informacion desde existe
            Tratamiento tratamiento= existe.get();
            //y despues la guarda en DetalleCompleto
            TratamientoDTO detalleCompleto = new TratamientoDTO();
            
            //Al tener el objeto creado le colocaremos los datos de la orden laboratorio creada para despues mostrarlo al final
            detalleCompleto.setNombre(tratamiento.getNombre());
            detalleCompleto.setDiagnostico(tratamiento.getDiagnostico());
            detalleCompleto.setFechaRevision(tratamiento.getFechaRevision());
            detalleCompleto.setMedicacion(tratamiento.getMedicacion());
            detalleCompleto.setObservacion(tratamiento.getObservacion());

            //Aqui usamos la comunicacion de microservicios , aqui con los metodos del client traemos la informacion desde los otros servicios
            MascotaDTO mascota = obtenerMascota(tratamiento.getCodigoMicrochip()).orElse(null);

            if(mascota == null){
                throw new RuntimeException("Error : no se pudo acceder a la informacion de la mascota registrada o hay problema en los otros servicios");
            }
            //Y aqui colocamos la informacion faltante en el detalle de la orden laboratorio
            detalleCompleto.setMascota(mascota);
        
            //Y aqui al final devolvemos el detalle completo con toda la informacion que necesitemos
            return Optional.of(detalleCompleto);

        } catch (Exception e) {
            System.err.println("Error al armar el detalle completo de el tratamiento: " + e.getMessage());
            throw new RuntimeException("Error interno al procesar el detalle de el tratamiento");
        }
    }

    // Actualizar
    public Tratamiento actualizarTratamiento(String nombre, Tratamiento tratamiento) {

        if (tratamiento.getDiagnostico() == null || tratamiento.getDiagnostico().trim().isEmpty()) {
                throw new IllegalArgumentException("El diagnostico es obligatorio.");
        }

        if (tratamiento.getFechaRevision() == null ) {
                throw new IllegalArgumentException("La fecha es obligatorio.");
        }

        if (tratamiento.getMedicacion() == null || tratamiento.getMedicacion().trim().isEmpty()) {
                throw new IllegalArgumentException("La Medicacio es obligatorio.");
        }

        if (tratamiento.getObservacion() == null || tratamiento.getObservacion().trim().isEmpty()) {
                throw new IllegalArgumentException("La Observacion es obligatorio.");
        }


        try {
            // Buscamos el tratamiento que ya existe en la BD
            Tratamiento tratamientoB = tratamientoRepository.findByNombre(nombre).orElse(null);

           
            // Validación
            if (tratamientoB != null) {
                tratamientoB.setDiagnostico(tratamiento.getDiagnostico());
                tratamientoB.setFechaRevision(tratamiento.getFechaRevision());
                tratamientoB.setMedicacion(tratamiento.getMedicacion());
                tratamientoB.setObservacion(tratamiento.getObservacion());
                tratamientoB.setCodigoMicrochip(tratamientoB.getCodigoMicrochip());
                
                // No se actualizan claves foraneas como el id del propietario

                return tratamientoRepository.save(tratamientoB);
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            System.err.println("Error de base de datos al actualizar: " + e.getMessage());
            throw new RuntimeException("Error interno al actualizar los datos");
        }
    }

}
