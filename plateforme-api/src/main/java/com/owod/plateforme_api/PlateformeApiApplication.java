package com.owod.plateforme_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PlateformeApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlateformeApiApplication.class, args);
	}

}
