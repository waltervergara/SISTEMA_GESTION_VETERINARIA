package Registro.Citas.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import Registro.Citas.Modelo.MascotaDTO;


@FeignClient(name = "registro")
public interface MascotaClient {

    @GetMapping("/api/v1/registro/mascotas/buscar/{codigoMicrochip}")
    MascotaDTO obtenerMascotaporCodigo(@PathVariable("codigoMicrochip") String codigoMicrochip);

}
