package com.example.Identificacion.Client;

import com.example.Identificacion.Dto.PropietarioRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "propietario-service", url = "${propietario.service.url}")
public interface PropietarioClient {

    @PostMapping("/api/v1/registro/propietarios") 
    void crearPropietario(@RequestBody PropietarioRequest request);
}