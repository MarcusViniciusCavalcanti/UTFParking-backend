package br.edu.utfpr.tsi.utfparking.rest.representations;

import br.edu.utfpr.tsi.utfparking.structure.dtos.TypeUserDTO;
import lombok.Data;

@Data
public class TypeUserRepresentation {
    private String name;
    private String translateName;

    public void copyAttributeBy(TypeUserDTO entity) {
        name = entity.name();
        translateName = entity.getTranslaterName();
    }
}
