package com.example.Identificacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class IdentificacionApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdentificacionApplication.class, args);
	}

}
