package com.hospitalizacion.hospitalizacion.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import com.hospitalizacion.hospitalizacion.model.*;
import com.hospitalizacion.hospitalizacion.repository.HospitalizadoRepository;

import feign.FeignException;

import com.hospitalizacion.hospitalizacion.client.*;

@Service
public class HospitalizadoService {

    @Autowired
    private HospitalizadoRepository hospitalizadoRepository;

    @Autowired 
    private MascotaClient mascotaClient;

    
    public Optional<Hospitalizado> guardarHospitalizacion(Hospitalizado hospitalizado) {
    
    
        Optional<Hospitalizado> existe = hospitalizadoRepository.findByCodigoHospitalizacion(hospitalizado.getCodigoHospitalizacion());
        if(existe.isPresent()){
            // Lanzamos una excepción personalizada en lugar de Optional.empty()
            throw new RuntimeException("Ya existe una Hospitalizado con el código: " + hospitalizado.getCodigoHospitalizacion());
        }

        try{
            //llamamos al microservicio de mascota
            
            mascotaClient.obtenerMascotaporCodigo(hospitalizado.getCodigoMicrochip());
        } catch (FeignException.NotFound e){
            throw new RuntimeException("Error: La mascota con el codigo " + hospitalizado.getCodigoMicrochip() + " no existe o no se encontrado");
        }

        //Si pasó todas las validaciones, guardamos en la base de datos
        try {
            return Optional.of(hospitalizadoRepository.save(hospitalizado));
        } catch (DataAccessException e) {
            System.err.println("Error de base de datos al intentar registrar la hospitalizacion: " + e.getMessage());
            throw new RuntimeException("Error interno al guardar en la base de datos");
        } catch(Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            throw new RuntimeException("Error inesperado en el servidor");
        }
    }

    //Buscar por codigoConsulta
    public Optional<Hospitalizado> buscarPorCodigoHospitalizacion(String codigoHospitalizacion){
        try{
            return hospitalizadoRepository.findByCodigoHospitalizacion(codigoHospitalizacion);
        }catch(Exception e){
            System.err.println("Error al buscar el codigo de la Hospitalizacion " + e.getMessage());
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

    public Optional<HospitalizadoDTO> obtenerDetalleCompletoHospitalizacion(String codigoHospitalizacion) {
        try {
            //Aqui buscamos la consulta que se guarda premiamente en base de datos
            Optional<Hospitalizado> existe = buscarPorCodigoHospitalizacion(codigoHospitalizacion);
            
            //si no la encuentra se detiene y no devuelve nada 
            if (existe.isEmpty()) {
                return Optional.empty(); 
            }
            
            //si la encuentra saca la informacion desde existe
            Hospitalizado hospitalizado = existe.get();
            //y despues la guarda en DetalleCompleto
            HospitalizadoDTO detalleCompleto = new HospitalizadoDTO();
            
            //Al tener el objeto creado le colocaremos los datos de la hospitalizacion creada para despues mostrarlo al final
            detalleCompleto.setCodigoHospitalizacion(hospitalizado.getCodigoHospitalizacion());
            detalleCompleto.setSala(hospitalizado.getSala());;
            detalleCompleto.setHora_monitoreo(hospitalizado.getHoraMonitoreo());;
            detalleCompleto.setDescripcion(hospitalizado.getDescripcion());;

            //Aqui usamos la comunicacion de microservicios , aqui con los metodos del client traemos la informacion desde los otros servicios
            MascotaDTO mascota = obtenerMascota(hospitalizado.getCodigoMicrochip()).orElse(null);

            if(mascota == null){
                throw new RuntimeException("Error : no se pudo acceder a la informacion de la mascota registrada o hay problema en los otros servicios");
            }
            //Y aqui colocamos la informacion faltante en el detalle de la hospitalizacion
            detalleCompleto.setMascotaDTO(mascota);
        
            //Y aqui al final devolvemos el detalle completo con toda la informacion que necesitemos
            return Optional.of(detalleCompleto);

        } catch (Exception e) {
            System.err.println("Error al armar el detalle completo de la Hospitalizacion: " + e.getMessage());
            throw new RuntimeException("Error interno al procesar el detalle de la Hospitalizacion");
        }
    }

}

