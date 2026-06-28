package com.example.Identificacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
public class IdentificacionApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdentificacionApplication.class, args);
	}

}
