package com.pagos.facturacion.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.pagos.facturacion.Client.PropietarioClient;
import com.pagos.facturacion.Repository.FacturaRepository;
import com.pagos.facturacion.Model.Factura;
import com.pagos.facturacion.Model.FacturaDTO;
import com.pagos.facturacion.Model.PropietarioDTO;

import feign.FeignException;

@Service
public class FacturaService {
    
    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired 
    private PropietarioClient propietarioClient;


    public Optional<Factura> guardarFactura(Factura factura) {
    
    
        Optional<Factura> existe = facturaRepository.findByCodigoFactura(factura.getCodigoFactura());
        if(existe.isPresent()){
            // Lanzamos una excepción personalizada en lugar de Optional.empty()
            throw new RuntimeException("Ya existe una factura con el código: " + factura.getCodigoFactura());
        }

        try{
            //llamamos al microservicio de propietario
            propietarioClient.obtenerPropietarioporRun(factura.getRunPropietario());
        } catch (FeignException.NotFound e){
            throw new RuntimeException("Error: el propietario con el run " + factura.getRunPropietario() + " no existe o no se encontrado");
        }

        //Si pasó todas las validaciones, guardamos en la base de datos
        try {
            return Optional.of(facturaRepository.save(factura));
        } catch (DataAccessException e) {
            System.err.println("Error de base de datos al intentar registrar la factura: " + e.getMessage());
            throw new RuntimeException("Error interno al guardar en la base de datos");
        } catch(Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            throw new RuntimeException("Error inesperado en el servidor");
        }
    }

    //Buscar por codigo factura de la factura
    public Optional<Factura> buscarPorCodigoFactura(String codigoFactura){
        try{
            return facturaRepository.findByCodigoFactura(codigoFactura);
        }catch(Exception e){
            System.err.println("Error al buscar el nombre de la factura " + e.getMessage());
            throw new RuntimeException("Error al consultar la base de datos");
        }
    }

    public Optional<PropietarioDTO> obtenerPropietario(String run){
        try{
            PropietarioDTO propietario = propietarioClient.obtenerPropietarioporRun(run);
            return Optional.ofNullable(propietario);
        } catch (Exception e){
            System.err.println("Error al obtener los datos del propietario " + e.getMessage());
            return Optional.empty();
        } 
    }

    public Optional<FacturaDTO> obtenerDetalleCompletoFactura(String codigoFactura) {
        try {
            //Aqui buscamos la consulta que se guarda premiamente en base de datos
            Optional<Factura> existe = buscarPorCodigoFactura(codigoFactura);
            
            //si no la encuentra se detiene y no devuelve nada 
            if (existe.isEmpty()) {
                return Optional.empty(); 
            }
            
            //si la encuentra saca la informacion desde existe
            Factura factura= existe.get();
            //y despues la guarda en DetalleCompleto
            FacturaDTO detalleCompleto = new FacturaDTO();
            
            //Al tener el objeto creado le colocaremos los datos de la Factura creada para despues mostrarlo al final
            detalleCompleto.setCodigoFactura(factura.getCodigoFactura());
            detalleCompleto.setPrecio(factura.getPrecio());
            detalleCompleto.setFechaEmision(factura.getFechaEmision());
            detalleCompleto.setDetalles(factura.getDetalles());


            //Aqui usamos la comunicacion de microservicios , aqui con los metodos del client traemos la informacion desde los otros servicios
            PropietarioDTO propietario = obtenerPropietario(factura.getRunPropietario()).orElse(null);
            //Y aqui colocamos la informacion faltante en el detalle de la Factura
            if (propietario == null){
                throw new RuntimeException("Error : no se pudo obtener la informacion del propietario. Uno o más servicios no estan operativos");

            }
            detalleCompleto.setPropietario(propietario);
        
            //Y aqui al final devolvemos el detalle completo con toda la informacion que necesitemos
            return Optional.of(detalleCompleto);

        } catch (Exception e) {
            System.err.println("Error al armar el detalle completo de la factura: " + e.getMessage());
            throw new RuntimeException("Error interno al procesar el detalle de la factura");
        }
    }

    

}