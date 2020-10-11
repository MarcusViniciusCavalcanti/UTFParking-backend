package br.edu.utfpr.tsi.utfparking.application.service.impl;

import br.edu.utfpr.tsi.utfparking.application.service.ApplicationConfigurationService;
import br.edu.utfpr.tsi.utfparking.domain.configuration.service.ApplicationConfigService;
import br.edu.utfpr.tsi.utfparking.structure.dtos.ApplicationConfigDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputApplicationConfiguration;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.TypeModeSystem;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApplicationConfigurationServiceImpl implements ApplicationConfigurationService {

    private final ApplicationConfigService applicationConfigService;

    @Override
    public ApplicationConfigDTO save(InputApplicationConfiguration config) {
        return applicationConfigService.save(config);
    }

    @Override
    public ApplicationConfigDTO updateModeSystem(TypeModeSystem modeSystem) {
        String ip = loadConfig().getIp();

        InputApplicationConfiguration inputApplicationConfiguration = new InputApplicationConfiguration();
        inputApplicationConfiguration.setIp(ip);
        inputApplicationConfiguration.setModeSystem(modeSystem);

        return save(inputApplicationConfiguration);
    }

    @Override
    public ApplicationConfigDTO loadConfig() {
        return applicationConfigService.loadConfig();
    }
}
