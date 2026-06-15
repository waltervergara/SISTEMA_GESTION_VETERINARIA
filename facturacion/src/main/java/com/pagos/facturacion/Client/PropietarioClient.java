package com.pagos.facturacion.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.pagos.facturacion.Model.PropietarioDTO;
//importante , el url tiene que ser el del microservicio que quieres traer , ademas aqui como tal se manda una solicitud para ocupar algo de ese microservisio
@FeignClient(name = "microservicio-registro-propietario" , url = "http://localhost:8080")
public interface PropietarioClient {
    //aqui el metodo que vas a ocupar tiene que tener el getmapping principal y el del metodo
    @GetMapping("/api/v1/registro/propietarios/buscar/{run}")
    PropietarioDTO obtenerPropietarioporRun(@PathVariable("run") String runPropietario);
}
