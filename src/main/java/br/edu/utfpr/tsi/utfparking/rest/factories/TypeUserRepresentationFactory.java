package br.edu.utfpr.tsi.utfparking.rest.factories;

import br.edu.utfpr.tsi.utfparking.rest.representations.TypeUserRepresentation;
import br.edu.utfpr.tsi.utfparking.structure.dtos.TypeUserDTO;
import org.springframework.stereotype.Component;

@Component
public class TypeUserRepresentationFactory {

    public TypeUserRepresentation toModel(TypeUserDTO model) {
        final TypeUserRepresentation typeUserRepresentation = new TypeUserRepresentation();
        typeUserRepresentation.copyAttributeBy(model);
        return typeUserRepresentation;
    }
}
