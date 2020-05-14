package br.edu.utfpr.tsi.utfparking.application.service.impl;

import br.edu.utfpr.tsi.utfparking.application.service.ApplicationConfigurationService;
import br.edu.utfpr.tsi.utfparking.domain.configuration.service.ApplicationConfigService;
import br.edu.utfpr.tsi.utfparking.structure.dtos.ApplicationConfigDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputApplicationConfiguration;
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
    public ApplicationConfigDTO loadConfig() {
        return applicationConfigService.loadConfig();
    }
}
