package com.example.Identificacion.Client;

import com.example.Identificacion.Dto.EmpleadoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "empleado-service", url = "${empleados.service.url}")
public interface EmpleadoClient {

    @GetMapping("/api/v1/empleados/correo/{email}") 
    EmpleadoResponse obtenerEmpleadoPorCorreo(@PathVariable("email") String email);
}