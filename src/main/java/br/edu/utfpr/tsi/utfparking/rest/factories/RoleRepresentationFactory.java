package br.edu.utfpr.tsi.utfparking.rest.factories;

import br.edu.utfpr.tsi.utfparking.rest.endpoints.RoleController;
import br.edu.utfpr.tsi.utfparking.rest.representations.RoleRepresentation;
import br.edu.utfpr.tsi.utfparking.structure.dtos.RoleDTO;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class RoleRepresentationFactory extends RepresentationModelAssemblerSupport<RoleDTO, RoleRepresentation> {

    public RoleRepresentationFactory() {
        super(RoleController.class, RoleRepresentation.class);
    }

    @Override
    public RoleRepresentation toModel(RoleDTO entity) {
        var representation = createModelWithId(entity.getId(), entity);
        representation.copyAttributeBy(entity);
        return representation;
    }
}
