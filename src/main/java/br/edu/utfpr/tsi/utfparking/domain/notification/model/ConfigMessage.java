package br.edu.utfpr.tsi.utfparking.domain.notification.model;

import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputApplicationConfiguration;
import lombok.Builder;

@Builder
public class ConfigMessage implements Message<InputApplicationConfiguration.TypeModeSystem> {

    private InputApplicationConfiguration.TypeModeSystem modeSystem;

    @Override
    public InputApplicationConfiguration.TypeModeSystem message() {
        return modeSystem;
    }
}
