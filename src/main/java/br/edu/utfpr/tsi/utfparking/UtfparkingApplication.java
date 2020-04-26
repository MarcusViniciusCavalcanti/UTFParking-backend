package br.edu.utfpr.tsi.utfparking;

import br.edu.utfpr.tsi.utfparking.domain.security.properties.JwtConfiguration;
import br.edu.utfpr.tsi.utfparking.domain.users.config.PropertiesDevelopment;
import br.edu.utfpr.tsi.utfparking.structure.disk.properties.DiskProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(value = {
        JwtConfiguration.class,
        PropertiesDevelopment.class,
        DiskProperties.class
})
public class UtfparkingApplication implements CommandLineRunner {

    private final DiskProperties diskProperties;

    public UtfparkingApplication(DiskProperties diskProperties) {
        this.diskProperties = diskProperties;
    }

    public static void main(String[] args) {
        SpringApplication.run(UtfparkingApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        var rootPathAvatar = Path.of(diskProperties.getPath());
        if(!Files.exists(rootPathAvatar)) {
            log.info("Criando diretório de avatar");
            Files.createDirectories(rootPathAvatar);
        }

    }
}
