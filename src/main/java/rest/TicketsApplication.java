package rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@SpringBootApplication
@EnableScheduling
public class TicketsApplication {

    public static void main(String[] args) {
        //System.setProperty("server.port", System.getenv().getOrDefault("PORT", "8080"));
        SpringApplication.run(TicketsApplication.class, args);

    }
}
