package br.edu.utfpr.tsi.utfparking.domain.configuration.service;

import br.edu.utfpr.tsi.utfparking.domain.configuration.entity.ApplicationConfig;
import br.edu.utfpr.tsi.utfparking.domain.notification.model.ConfigMessage;
import br.edu.utfpr.tsi.utfparking.domain.notification.model.TopicApplication;
import br.edu.utfpr.tsi.utfparking.domain.notification.service.SendingMessageService;
import br.edu.utfpr.tsi.utfparking.structure.dtos.ApplicationConfigDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputApplicationConfiguration;
import br.edu.utfpr.tsi.utfparking.structure.repositories.ApplicationConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApplicationConfigService {

    private final ApplicationConfigRepository applicationConfigRepository;

    private final SendingMessageService sendingMessageService;

    public ApplicationConfigDTO loadConfig() {
        var applicationConfig = applicationConfigRepository.findById(1).orElseGet(() -> {
            var defaultApplicationConfiguration = getDefaultApplicationConfiguration();
            applicationConfigRepository.save(defaultApplicationConfiguration);

            return defaultApplicationConfiguration;
        });

        return ApplicationConfigDTO.builder()
                .ip(applicationConfig.getIp())
                .modeSystem(applicationConfig.getModeSystem())
                .build();
    }

    @Transactional
    public ApplicationConfigDTO save(InputApplicationConfiguration config) {
        var applicationConfig = applicationConfigRepository.findById(1).orElse(getDefaultApplicationConfiguration());

        applicationConfig.setModeSystem(config.getModeSystem().name());
        applicationConfig.setIp(config.getIp());

        var configMessage = ConfigMessage.builder()
                .modeSystem(InputApplicationConfiguration.TypeModeSystem.valueOf(applicationConfig.getModeSystem()))
                .build();

        sendingMessageService.sendBeforeTransactionCommit(configMessage, TopicApplication.CONFIG.getTopicName());
        applicationConfigRepository.save(applicationConfig);

        return ApplicationConfigDTO.builder()
                .ip(applicationConfig.getIp())
                .modeSystem(applicationConfig.getModeSystem())
                .build();
    }

    private ApplicationConfig getDefaultApplicationConfiguration() {
        var applicationConfig = new ApplicationConfig(1, InputApplicationConfiguration.TypeModeSystem.NONE.name());
        applicationConfig.setIp("0.0.0.0");
        return applicationConfig;
    }
}
