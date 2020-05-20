package br.edu.utfpr.tsi.utfparking.rest.representations;

import br.edu.utfpr.tsi.utfparking.rest.endpoints.ConfigController;
import br.edu.utfpr.tsi.utfparking.structure.dtos.ApplicationConfigDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApplicationConfigurationRepresentation extends RepresentationModel<ApplicationConfigurationRepresentation> {
    
    private String modeSystem;
    
    private String ip;
    
    public void copyAttributeBy(ApplicationConfigDTO dto) {
        this.modeSystem = dto.getModeSystem();
        this.ip = dto.getIp();

        removeLinks();

        add(linkTo(methodOn(ConfigController.class).loadConfig()).withRel("load"));
        add(linkTo(methodOn(ConfigController.class).save(null, null)).withRel("save"));

    }
}
