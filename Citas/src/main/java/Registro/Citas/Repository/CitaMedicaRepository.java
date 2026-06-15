package Registro.Citas.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Registro.Citas.Modelo.CitaMedica;

@Repository
public interface CitaMedicaRepository extends JpaRepository<CitaMedica,Long> {

    Optional<CitaMedica> findByCodigoConsulta(String codigoConsulta);

    List<CitaMedica> findByCodigoMicrochip(String codigoMicrochip);
}
