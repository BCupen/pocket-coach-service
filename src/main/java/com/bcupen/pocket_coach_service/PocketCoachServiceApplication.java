package com.bcupen.pocket_coach_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PocketCoachServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PocketCoachServiceApplication.class, args);
	}

}
