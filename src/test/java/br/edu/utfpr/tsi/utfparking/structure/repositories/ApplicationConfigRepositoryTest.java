package br.edu.utfpr.tsi.utfparking.structure.repositories;

import br.edu.utfpr.tsi.utfparking.domain.configuration.entity.ApplicationConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ApplicationConfigRepositoryTest {

    @Autowired
    private ApplicationConfigRepository applicationConfigRepository;

    @Test
    void saveConfig() {
        var applicationConfig = new ApplicationConfig();

        applicationConfig.setId(1);
        applicationConfig.setModeSystem("Model System");
        applicationConfig.setIp("192.168.0.1");

        var save = applicationConfigRepository.save(applicationConfig);
        assertNotNull(save);
    }
}
