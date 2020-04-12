package br.edu.utfpr.tsi.utfparking;

import br.edu.utfpr.tsi.utfparking.domain.security.properties.JwtConfiguration;
import br.edu.utfpr.tsi.utfparking.domain.users.config.PropertiesDevelopment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = {
        JwtConfiguration.class,
        PropertiesDevelopment.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UtfparkingApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(UtfparkingApplication.class, args);
    }

    @Override
    public void run(String... args) {

    }
}
