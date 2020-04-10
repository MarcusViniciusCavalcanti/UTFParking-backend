package br.edu.utfpr.tsi.utfparking.rest.representations;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RoleRepresentation extends RepresentationModel<RoleRepresentation> {
    private Long roleId;

    private String name;

    private String description;
}
