package Microservicio.Registro.Repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Microservicio.Registro.Modelo.Mascota;



@Repository
public interface MascotaRepository extends JpaRepository<Mascota , String> { //JpaRepository trae lista una serie de metodos para el crud , ademas de dejar crear consultas personalisada de foram simplre
   
    //busqueda personalizada
    Optional<Mascota> findByCodigoMicrochip(String codigoMicrochip);
}
