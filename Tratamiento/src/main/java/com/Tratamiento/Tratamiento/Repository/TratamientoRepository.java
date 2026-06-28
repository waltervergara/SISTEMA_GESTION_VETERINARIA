package com.Tratamiento.Tratamiento.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Tratamiento.Tratamiento.Model.Tratamiento;



@Repository
public interface TratamientoRepository extends JpaRepository<Tratamiento,Long> {
    Optional<Tratamiento>findByNombre(String nombre);
}
