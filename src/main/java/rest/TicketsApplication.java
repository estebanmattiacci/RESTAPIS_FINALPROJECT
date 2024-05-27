package rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@SpringBootApplication
public class TicketsApplication {

    public static void main(String[] args) {
        //System.setProperty("server.port", System.getenv().getOrDefault("PORT", "8080"));
        SpringApplication.run(TicketsApplication.class, args);

    }
}
