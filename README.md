# Spring Boot Configuration

## Basic  
### Using Application Properties  
Spring Boot automatically loads the `application.properties` whenever it starts up.  
You can dereference values from the property file in your java code through the environment.  
Put a property in the application.properties file, like this.
```text
message-from-application-properties=Hello from application.properties
```
Now, let’s edit the code to read in that value.  
```java
package com.example.demo;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@Log4j2
@SpringBootApplication
public class ConfigurationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigurationApplication.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner(Environment environment) {
        return args -> {
            log.info("message from app.properties " + environment.getProperty("message-from-application-properties"));
        };
    }
}
```

### Using Properties outside the Application  
You can use a program argument or an `environment variable` to fill the `spring.config.name` property.
```shell script
C:\repos\spring-configuration-tutorial>java -jar -Dspring.config.name=foo .\target\tutorial-0.0.1-SNAPSHOT.jar
```
Re-run the application now with that environment variable in scope, and it’ll fail because it’ll try to load `foo.properties`, 
not `application.properties`. 
```ignorelang
2020-05-03 07:43:36.505  INFO 27132 --- [           main] c.e.d.ConfigurationTutorialApplication   : message from application.properties null
``` 

You could also run the application with the configuration that lives outside the application, 
adjacent to the jar, like this.   
```ignorelang
.
|-- application.properties
|__ tutorial-0.0.1-SNAPSHOT.jar
```
If you run the application like this, 
the values in the external applicatin.properties will override the values inside the .jar.  
```ignorelang
C:\repos\spring-configuration-tutorial>java -jar  .\target\tutorial-0.0.1-SNAPSHOT.jar  

2020-05-03 07:50:56.254  INFO 7924 --- [           main] c.e.d.ConfigurationTutorialApplication   : message from application.properties Hello from application.properties ou
tside of the jar

```
**TODO** - Add order of loading properties from Spring doc  

## Application Profiles  
Spring Boot is aware of Spring profiles, as well. If profile is active, Spring loads `application-<profile>. properties`  
Let's create `application-dev.propertires` with the following:
```text
message-from-application-properties=Hello from dev application.properties
```  

```shell script
C:\repos\spring-configuration-tutorial\profiles>java -jar  -Dspring.profiles.active=dev .\target\profiles-0.0.1-SNAPSHOT.jar

2020-05-03 08:36:34.923  INFO 8492 --- [           main] s.c.t.p.ConfigurationTutorialApplication : message from application.properties Hello from dev application.propertie
s
```

## Using @Value Annotation  
You can also use `@Value` annotation to inject the value as a parameter.  
You can also specify default values to be returned if there are no other values that match.
```java
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
                                        @Value("${mesage-from-application-properties:OOPS!}") String valueDoesNotExist) {
        return args -> {
            log.info("message from application.properties " + valueDoesExist);
            log.info("missing message from application.properties " + valueDoesNotExist);
        };
    }
}
```
Also, note that the default String that you provide can, in turn, interpolate some other property.   
So you could do something like this, assuming a key like default-error-message does exist somewhere in your application 
configuration:
```${mesage-from-application-properties:${default-error-message:YIKES!}}```

Assume we defined the following properties:
```text
application-dev.properties
message-from-application-properties=Hello from dev application.properties

application-qa.properties
message-from-application-properties=Hello from qa application.properties
default-error-message:ERROR!
```
Runnig with `dev` profile will result in the folowing:
```shell script
C:\repos\spring-configuration-tutorial\value>java -jar  -Dspring.profiles.active=dev .\target\value-0.0.1-SNAPSHOT.jar

2020-05-03 09:10:37.834  INFO 10224 --- [           main] s.c.t.v.ConfigurationTutorialApplication : message from application.properties Hello from dev application.properti
es
2020-05-03 09:10:37.836  INFO 10224 --- [           main] s.c.t.v.ConfigurationTutorialApplication : missing message from application.properties YIKES!
```
Runnig with `qa` profile will result in the folowing:
```shell script
C:\repos\spring-configuration-tutorial\value>java -jar  -Dspring.profiles.active=qa .\target\value-0.0.1-SNAPSHOT.jar

2020-05-03 09:13:42.900  INFO 27980 --- [           main] s.c.t.v.ConfigurationTutorialApplication : message from application.properties Hello from qa application.propertie
s
2020-05-03 09:13:42.916  INFO 27980 --- [           main] s.c.t.v.ConfigurationTutorialApplication : missing message from application.properties ERROR!
```

## Using `PropertySource`  
You might want to do something like this if you wish to, for example, to integrate your application with the configuration 
you’re storing in an external database or a directory or some other things about which Spring Boot doesn’t automatically know.
```java
package com.example.configuration.propertysource;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.PropertySource;

@Log4j2
@SpringBootApplication
public class ConfigurationApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
            .sources(ConfigurationApplication.class)
            .initializers(context -> context
                .getEnvironment()
                .getPropertySources()
                .addLast(new BootifulPropertySource())
            )
            .run(args);
    }

    @Bean
    ApplicationRunner applicationRunner(@Value("${bootiful-message}") String bootifulMessage) {
        return args -> {
            log.info("message from custom PropertySource: " + bootifulMessage);
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
```

## Using `ConfigurationProperties`  
It might be helpful to have these values bound to an object automatically.  
This is precisely what Spring Boots ConfigutationProperties do for you. Let’s see this in action.

Ley’s say that ou ave an application.properties file with the following property:

```
bootiful.message = Hello from a @ConfiguratinoProperties COPY
```

Then you can run the application and see that the configuration value has been bound to the object for us:  
```java
package com.example.configuration.cp;

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
public class ConfigurationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigurationApplication.class, args);
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
```

## Using Config Server
We’ve seen that Spring can load configuration adjacent to the application .jar, and that it can load the configuration 
from environment variables and program arguments.  
It’s not hard to get information into a Spring Boot application, but its sort of piecemeal.  
It’s hard to version control environment variables or to secure program arguments.

### Config Server  
To solve some of these problems, the Spring Cloud team built the spring CLoud Configuration Server.  
The Spring Cloud Config Server is an HTTP API that fronts a backend storage engine.  
The storage s pluggable, with the most common being a Git repository, though there is support for others as well. 
These include Subversion, a local file system, and even MongDB.

We’re going to need to do two things to make it work: first, we must use `@EnableConfigServer` annotation and then 
provide a configuration value to point it to the Git repository with our configuration file. 
Here are the `application.properties`.
```
spring.cloud.config.server.git.uri=https://github.com/joshlong/greetings-config-repository.git
server.port=8888
```
And here’s what your main class should look like.
```java
package com.example.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

### Config Client  
To start the Spring Cloud Config Server, it’ll need to have some - you guessed it! - configuration.  
This configuration needs to be evaluated earlier, before the rest of the configuration.  
You can put this configuration in a file called `bootstrap.properties`.

You’ll need to identify your application to give it a name so that when it connects to the Spring Cloud Config Server, 
it will know which configuration to provide us.  
The name we specify here will be matched to a property file in the Git repository (bootiful.properties in our case).  
Here’s what you should put in the bootstrap.properties file.

```text
spring.cloud.config.uri=http://localhost:8888
spring.application.name=bootiful
```

