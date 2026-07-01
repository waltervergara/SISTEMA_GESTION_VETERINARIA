package com.example.Identificacion.Client;

import com.example.Identificacion.Dto.EmpleadoRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Apuntamos a la URL EXACTA de tu controlador de Empleados
@FeignClient(name = "empleado")
public interface EmpleadoClient {

    @PostMapping("/api/v1/registro/empleados") // ¡URL CORREGIDA!
    void crearEmpleado(@RequestBody EmpleadoRequest request);
}