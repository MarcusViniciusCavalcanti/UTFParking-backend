package br.edu.utfpr.tsi.utfparking;

import br.edu.utfpr.tsi.utfparking.domain.security.properties.JwtConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = JwtConfiguration.class)
public class UtfparkingApplication {

    public static void main(String[] args) {
        SpringApplication.run(UtfparkingApplication.class, args);
    }

}
