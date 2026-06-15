package com.pagos.facturacion.Repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pagos.facturacion.Model.Factura;

@Repository
public interface FacturaRepository extends JpaRepository<Factura,Long> {
    Optional<Factura> findByCodigoFactura(String codigoFactura);
}