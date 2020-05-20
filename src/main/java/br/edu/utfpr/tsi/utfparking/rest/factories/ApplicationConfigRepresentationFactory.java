package br.edu.utfpr.tsi.utfparking.rest.factories;


import br.edu.utfpr.tsi.utfparking.rest.endpoints.ConfigController;
import br.edu.utfpr.tsi.utfparking.rest.endpoints.UsersController;
import br.edu.utfpr.tsi.utfparking.rest.representations.ApplicationConfigurationRepresentation;
import br.edu.utfpr.tsi.utfparking.rest.representations.UserRepresentation;
import br.edu.utfpr.tsi.utfparking.structure.dtos.ApplicationConfigDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.UserDTO;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class ApplicationConfigRepresentationFactory extends RepresentationModelAssemblerSupport<ApplicationConfigDTO, ApplicationConfigurationRepresentation> {

    public ApplicationConfigRepresentationFactory() {
        super(ConfigController.class, ApplicationConfigurationRepresentation.class);
    }

    @Override
    public ApplicationConfigurationRepresentation toModel(ApplicationConfigDTO entity) {
        var representation = createModelWithId(1, entity);
        representation.copyAttributeBy(entity);
        return representation;
    }
}
