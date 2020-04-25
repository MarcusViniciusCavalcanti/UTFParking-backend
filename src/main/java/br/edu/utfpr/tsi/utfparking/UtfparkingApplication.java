package br.edu.utfpr.tsi.utfparking;

import br.edu.utfpr.tsi.utfparking.domain.security.properties.JwtConfiguration;
import br.edu.utfpr.tsi.utfparking.domain.users.config.PropertiesDevelopment;
import br.edu.utfpr.tsi.utfparking.structure.disk.properties.DiskProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = {
        JwtConfiguration.class,
        PropertiesDevelopment.class,
        DiskProperties.class
})
public class UtfparkingApplication {

    public static void main(String[] args) {
        SpringApplication.run(UtfparkingApplication.class, args);
    }

}
