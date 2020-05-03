package com.example.spring.config.tutorial.value;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@Log4j2
@SpringBootApplication
public class ConfigurationTutorialApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(ConfigurationTutorialApplication.class)
                .run(args);
    }

    @Bean
    ApplicationRunner applicationRunner(Environment environment,
                                        @Value("${message-from-application-properties:OOPS!}") String valueDoesExist,
                                        @Value("${mesage-from-application-properties:${default-error-message:YIKES!}}") String valueDoesNotExist) {
        return args -> {
            log.info("message from application.properties " + valueDoesExist);
            log.info("missing message from application.properties " + valueDoesNotExist);
        };
    }
}
