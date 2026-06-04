package com.toystore;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Toy Store API",
				version = "1.0",
				description = "API for Toy Store mobile application"
		)
)
public class ToyStoreApplication {
	public static void main(String[] args) {
		SpringApplication.run(ToyStoreApplication.class, args);
	}
}