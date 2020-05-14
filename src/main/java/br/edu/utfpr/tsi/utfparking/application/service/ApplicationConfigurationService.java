package br.edu.utfpr.tsi.utfparking.application.service;

import br.edu.utfpr.tsi.utfparking.structure.dtos.ApplicationConfigDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputApplicationConfiguration;
import org.springframework.stereotype.Service;

@Service
public interface ApplicationConfigurationService {

    ApplicationConfigDTO save(InputApplicationConfiguration config);

    ApplicationConfigDTO loadConfig();
}
