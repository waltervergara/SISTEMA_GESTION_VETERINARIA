package com.registro.empleados.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.registro.empleados.model.Empleados;


@Repository
public interface EmpleadosRepository extends JpaRepository<Empleados,String > {
    //Metodo personalizado para encontrar por run 
    Optional<Empleados> findByRunEmpleado(String runEmpleado);
}
