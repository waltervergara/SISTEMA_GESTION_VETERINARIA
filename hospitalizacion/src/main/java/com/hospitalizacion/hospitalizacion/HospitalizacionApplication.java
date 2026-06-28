package com.hospitalizacion.hospitalizacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
public class HospitalizacionApplication {

	public static void main(String[] args) {
		SpringApplication.run(HospitalizacionApplication.class, args);
	}

}
