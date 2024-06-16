package rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"rest", "security", "config"})
public class TicketsApplication {

    public static void main(String[] args) {
        //System.setProperty("server.port", System.getenv().getOrDefault("PORT", "8080"));
        SpringApplication.run(TicketsApplication.class, args);

    }
}
