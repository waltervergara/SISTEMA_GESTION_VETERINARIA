package com.Tratamiento.Tratamiento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TratamientoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TratamientoApplication.class, args);
	}

}
