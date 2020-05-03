package com.example.configuration.tutorial.config.client;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Log4j2
@SpringBootApplication
public class ConfigClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigClientApplication.class, args);
	}

	@Bean
	ApplicationRunner applicationRunner(@Value("${message-from-config-server}") String configServer) {
		return args -> {
			log.info("message from the Spring Cloud Config Server: " + configServer);
		};
	}
}
