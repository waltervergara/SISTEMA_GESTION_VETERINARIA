package com.registro.laboratorio.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.registro.laboratorio.client.MascotaClient;
import com.registro.laboratorio.model.MascotaDTO;
import com.registro.laboratorio.model.LabOrden;
import com.registro.laboratorio.model.LabOrdenDTO;
import com.registro.laboratorio.repository.LabOrdenRepository;



import feign.FeignException;


@Service
public class LabOrdenService {

    @Autowired
    private LabOrdenRepository labOrdenRepository;
    

    @Autowired 
    private MascotaClient mascotaClient;

    public Optional<LabOrden> guardarLabOrden(LabOrden labOrden) {
    
    
        Optional<LabOrden> existe = labOrdenRepository.findByNombreOrden(labOrden.getNombreOrden());
        if(existe.isPresent()){
            // Lanzamos una excepción personalizada en lugar de Optional.empty()
            throw new RuntimeException("Ya existe una Hospitalizado con el código: " + labOrden.getNombreOrden());
        }

        try{
            //llamamos al microservicio de mascota
            mascotaClient.obtenerMascotaporCodigo(labOrden.getCodigoMicrochip());
        } catch (FeignException.NotFound e){
            throw new RuntimeException("Error: La mascota con el codigo " + labOrden.getCodigoMicrochip() + " no existe o no se encontrado");
        }

        //Si pasó todas las validaciones, guardamos en la base de datos
        try {
            return Optional.of(labOrdenRepository.save(labOrden));
        } catch (DataAccessException e) {
            System.err.println("Error de base de datos al intentar registrar la orden de laboratorio: " + e.getMessage());
            throw new RuntimeException("Error interno al guardar en la base de datos");
        } catch(Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            throw new RuntimeException("Error inesperado en el servidor");
        }
    }

    //Buscar por nombre de la orden laboratorio
    public Optional<LabOrden> buscarPorNombreLabOrden(String nombreOrden){
        try{
            return labOrdenRepository.findByNombreOrden(nombreOrden);
        }catch(Exception e){
            System.err.println("Error al buscar el codigo de la orden laboratorio " + e.getMessage());
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

    public Optional<LabOrdenDTO> obtenerDetalleCompletoLabOrden(String nombreOrden) {
        try {
            //Aqui buscamos la consulta que se guarda premiamente en base de datos
            Optional<LabOrden> existe = buscarPorNombreLabOrden(nombreOrden);
            
            //si no la encuentra se detiene y no devuelve nada 
            if (existe.isEmpty()) {
                return Optional.empty(); 
            }
            
            //si la encuentra saca la informacion desde existe
            LabOrden labOrden= existe.get();
            //y despues la guarda en DetalleCompleto
            LabOrdenDTO detalleCompleto = new LabOrdenDTO();
            
            //Al tener el objeto creado le colocaremos los datos de la orden laboratorio creada para despues mostrarlo al final
            detalleCompleto.setNombre(labOrden.getNombreOrden());
            detalleCompleto.setFecha_pedido(labOrden.getFechaPedido());
            detalleCompleto.setTipo_examen(labOrden.getTipoExamen());
            detalleCompleto.setDescripcion(labOrden.getDescripcion());
            detalleCompleto.setEstado(labOrden.getEstado());

            //Aqui usamos la comunicacion de microservicios , aqui con los metodos del client traemos la informacion desde los otros servicios
            MascotaDTO mascota = obtenerMascota(labOrden.getCodigoMicrochip()).orElse(null);

            if(mascota == null){
                throw new RuntimeException("Error : no se pudo acceder a la informacion de la mascota registrada o hay problema en los otros servicios");
            }
            //Y aqui colocamos la informacion faltante en el detalle de la orden laboratorio
            detalleCompleto.setMascotaDTO(mascota);
        
            //Y aqui al final devolvemos el detalle completo con toda la informacion que necesitemos
            return Optional.of(detalleCompleto);

        } catch (Exception e) {
            System.err.println("Error al armar el detalle completo de la orden laboratorio: " + e.getMessage());
            throw new RuntimeException("Error interno al procesar el detalle de la orden laboratorio");
        }
    }

    // Actualizar
    public LabOrden ActualizarLabOrde(String nombreOrden, LabOrden labOrden) {
        try {
            // Buscamos la mascota que ya existe en la BD
            LabOrden labOrdenbuscar = labOrdenRepository.findByNombreOrden(nombreOrden).orElse(null);

            if (labOrden.getEstado() == null || labOrden.getEstado().trim().isEmpty()) {
                throw new IllegalArgumentException("El estado es obligatorio.");
            }


            // Validación
            if (labOrdenbuscar != null) {
                labOrdenbuscar.setEstado(labOrden.getEstado());
                
                // No se actualizan claves foraneas como el id del propietario

                return labOrdenRepository.save(labOrdenbuscar);
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            System.err.println("Error de base de datos al actualizar: " + e.getMessage());
            throw new RuntimeException("Error interno al actualizar los datos");
        }
    }






}
