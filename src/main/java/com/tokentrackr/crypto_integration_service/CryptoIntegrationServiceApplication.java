package com.tokentrackr.crypto_integration_service;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableRabbit
@EnableScheduling
@EnableAsync
public class CryptoIntegrationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoIntegrationServiceApplication.class, args);
	}

}
