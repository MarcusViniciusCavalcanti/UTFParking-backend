package br.edu.utfpr.tsi.utfparking.domain.configuration.service;

import br.edu.utfpr.tsi.utfparking.domain.configuration.entity.ApplicationConfig;
import br.edu.utfpr.tsi.utfparking.domain.notification.service.SendingMessageService;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputApplicationConfiguration;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.TypeModeSystem;
import br.edu.utfpr.tsi.utfparking.structure.repositories.ApplicationConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ApplicationConfigService.class)
class ApplicationConfigServiceTest {

    @MockBean
    private ApplicationConfigRepository applicationConfigRepository;

    @MockBean
    private SendingMessageService sendingMessageService;

    @Autowired
    private ApplicationConfigService applicationConfigService;

    @BeforeEach
    void setup() {
        doNothing().when(sendingMessageService).sendBeforeTransactionCommit(any(), any());
    }

    @Test
    void shouldReturnDefaultConfigurations() {
        when(applicationConfigRepository.findById(anyInt())).thenReturn(Optional.empty());
        var applicationConfig = applicationConfigService.loadConfig();

        assertEquals(TypeModeSystem.NONE.name(), applicationConfig.getModeSystem());
        assertEquals("0.0.0.0", applicationConfig.getIp());
    }

    @Test
    void shouldReturnSavedConfiguration() {
        var config = new ApplicationConfig(1, TypeModeSystem.AUTOMATIC.name());
        when(applicationConfigRepository.findById(anyInt())).thenReturn(Optional.of(config));

        var applicationConfig = applicationConfigService.loadConfig();

        assertEquals(TypeModeSystem.AUTOMATIC.name(), applicationConfig.getModeSystem());
    }

    @Test
    void shouldSaveConfigurationWhenFindConfigurationOnDatabase() {
        var config = new ApplicationConfig(1, TypeModeSystem.AUTOMATIC.name());

        when(applicationConfigRepository.findById(anyInt())).thenReturn(Optional.of(config));

        var input = new InputApplicationConfiguration();

        input.setIp("ip");
        input.setModeSystem(TypeModeSystem.NONE);

        var save = applicationConfigService.save(input);

        assertEquals(TypeModeSystem.NONE.name(), save.getModeSystem());
        assertEquals("ip", save.getIp());
    }

    @Test
    void shouldSaveConfigurationWhenFindNotConfigurationOnDatabase() {
        when(applicationConfigRepository.findById(anyInt())).thenReturn(Optional.empty());

        var input = new InputApplicationConfiguration();

        input.setIp("ip");
        input.setModeSystem(TypeModeSystem.AUTOMATIC);

        var save = applicationConfigService.save(input);

        assertEquals(TypeModeSystem.AUTOMATIC.name(), save.getModeSystem());
        assertEquals("ip", save.getIp());
    }
}
