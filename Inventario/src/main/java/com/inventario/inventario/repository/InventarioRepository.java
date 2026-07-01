package com.inventario.inventario.repository;

import com.inventario.inventario.model.Inventario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario,Long> {
    Optional<Inventario> findByNombre(String nombre);
}
