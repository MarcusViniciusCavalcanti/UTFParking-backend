package br.edu.utfpr.tsi.utfparking.application.factories;


import br.edu.utfpr.tsi.utfparking.application.endpoints.UsersController;
import br.edu.utfpr.tsi.utfparking.application.representations.UserRepresentation;
import br.edu.utfpr.tsi.utfparking.structure.dtos.UserDTO;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class UserRepresentationFactory extends RepresentationModelAssemblerSupport<UserDTO, UserRepresentation> {

    public UserRepresentationFactory() {
        super(UsersController.class, UserRepresentation.class);
    }

    @Override
    public UserRepresentation toModel(UserDTO entity) {
        var representation = createModelWithId(entity.getId(), entity);
        representation.copyAttributeBy(entity);
        return representation;
    }
}
