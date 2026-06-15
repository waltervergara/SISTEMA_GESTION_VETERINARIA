package com.pagos.facturacion.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pagos.facturacion.Service.FacturaService;
import com.pagos.facturacion.Model.Factura;
import com.pagos.facturacion.Model.FacturaDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/factura")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    // 1. Guardar una nueva factura en la base de datos local
    @PostMapping("/guardar")
    public ResponseEntity<?> guardarLabOrden(@Valid @RequestBody Factura factura) {
        try {
            Optional<Factura> nuevaFactura = facturaService.guardarFactura(factura);
            
            if (nuevaFactura.isPresent()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(nuevaFactura.get());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Ya existe una factura registrada con ese código de consulta.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar la factura: " + e.getMessage());
        }
    }

    // 2. Buscar la factura completa ( propietario microservicio)
    @GetMapping("/detalle/{codigoFactura}")
    public ResponseEntity<?> obtenerDetalleLabOrden(@PathVariable String codigoFactura) {
        try {
            Optional<FacturaDTO> facturaCompleta = facturaService.obtenerDetalleCompletoFactura(codigoFactura);

            if (facturaCompleta.isPresent()) {
                return ResponseEntity.ok(facturaCompleta.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontró ninguna factura con el código: " + codigoFactura);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener los detalles de la factura: " + e.getMessage());
        }
    }

}


//http://localhost:8088/api/v1/factura/guardar
//http://localhost:8088/api/v1/factura/detalle/
