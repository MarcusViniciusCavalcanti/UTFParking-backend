package br.edu.utfpr.tsi.utfparking.structure.dtos.inputs;

import br.edu.utfpr.tsi.utfparking.rest.annotations.ValueOfEnum;
import lombok.Data;

@Data
public class UpdateModeSystem {
    @ValueOfEnum(enumClass = TypeModeSystem.class, message = "Invalid value of enum, only accept AUTOMATIC, NONE AND MANUAL value")
    private TypeModeSystem modeSystem;
}
