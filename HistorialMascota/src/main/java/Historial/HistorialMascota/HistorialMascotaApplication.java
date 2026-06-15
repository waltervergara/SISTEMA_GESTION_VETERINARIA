package Historial.HistorialMascota;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class HistorialMascotaApplication {

	public static void main(String[] args) {
		SpringApplication.run(HistorialMascotaApplication.class, args);
	}

}
