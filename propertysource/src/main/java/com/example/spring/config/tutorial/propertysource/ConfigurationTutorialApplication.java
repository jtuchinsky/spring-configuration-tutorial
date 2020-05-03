package com.example.spring.config.tutorial.propertysource;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

@Log4j2
@SpringBootApplication
public class ConfigurationTutorialApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(ConfigurationTutorialApplication.class)
                .initializers(context -> context
                        .getEnvironment()
                        .getPropertySources()
                        .addLast(new BootifulPropertySource())
                )
                .run(args);
    }

    @Bean
    ApplicationRunner applicationRunner(@Value("${bootiful-message:ERROR!!!}") String bootifulMessage) {
        return args -> {
            log.info("message from custom property source " + bootifulMessage);
        };
    }
}

class BootifulPropertySource extends PropertySource<String> {

    BootifulPropertySource() {
        super("bootiful");
    }

    @Override
    public Object getProperty(String name) {

        if (name.equalsIgnoreCase("bootiful-message")) {
            return "Hello from " + BootifulPropertySource.class.getSimpleName() + "!";
        }

        return null;
    }
}
