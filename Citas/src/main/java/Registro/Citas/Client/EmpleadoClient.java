package Registro.Citas.Client;

import Registro.Citas.Modelo.EmpleadosDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "empleados")
public interface EmpleadoClient {

    @GetMapping("/api/v1/registro/empleados/buscar/{runEmpleado}")
    EmpleadosDTO obtenerEmpleadoporRun(@PathVariable("runEmpleado") String runEmpleado);

}
