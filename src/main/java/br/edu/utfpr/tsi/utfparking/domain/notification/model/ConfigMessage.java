package br.edu.utfpr.tsi.utfparking.domain.notification.model;

import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.TypeModeSystem;
import lombok.Builder;

@Builder
public class ConfigMessage implements Message<TypeModeSystem> {

    private TypeModeSystem modeSystem;

    @Override
    public TypeModeSystem message() {
        return modeSystem;
    }
}
