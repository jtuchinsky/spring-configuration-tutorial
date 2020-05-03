package com.example.spring.config.tutorial.configuration.properties;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@Log4j2
@SpringBootApplication
@EnableConfigurationProperties(BootifulProperties.class)
public class ConfigurationTutorialApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigurationTutorialApplication.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner(BootifulProperties bootifulProperties) {
        return args -> {
            log.info("message from @ConfigurationProperties " + bootifulProperties.getMessage());
        };
    }
}

@Data
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties("bootiful")
class BootifulProperties {
    private final String message;
}

