package com.registro.laboratorio.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.registro.laboratorio.model.LabOrden;



@Repository
public interface LabOrdenRepository extends JpaRepository<LabOrden,Long> {
    Optional<LabOrden>findByNombreOrden(String nombreOrden);
}
