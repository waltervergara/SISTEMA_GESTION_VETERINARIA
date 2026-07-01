package Historial.HistorialMascota.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Historial.HistorialMascota.Modelo.Historial;

@Repository
public interface HistorialRepository extends JpaRepository<Historial, Long>{
    Optional<Historial> findByCodigoMicrochip(String codigoMicrochip);
}