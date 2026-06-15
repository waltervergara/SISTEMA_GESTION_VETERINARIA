package com.example.Identificacion.Repository;

import com.example.Identificacion.Model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    // Método para buscar un rol por su nombre (ej: "ROLE_VET")
    Optional<Rol> findByName(String name);
}