package shareit.logging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class LoggingServerApp {
    public static void main(String[] args) {
        SpringApplication.run(LoggingServerApp.class, args);
    }
}
