package Microservicio.Registro.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Microservicio.Registro.Modelo.Propietario;
import java.util.Optional;






@Repository
public interface PropietarioRepository extends JpaRepository<Propietario,String> {//JpaRepository trae lista una serie de metodos para el crud , ademas de dejar crear consultas personalisada de foram simplre

    //Metodo de busqueda por rut(Identificador unico)
    Optional<Propietario>findByRunPropietario(String run_propietario);

}
