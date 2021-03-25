package br.edu.utfpr.tsi.utfparking.rest.representations;

import br.edu.utfpr.tsi.utfparking.structure.dtos.RoleDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@NoArgsConstructor
@Setter
@AllArgsConstructor
public class RoleRepresentation extends RepresentationModel<RoleRepresentation> {
    private Long roleId;

    private String name;

    private String description;

    public void copyAttributeBy(RoleDTO entity) {
        roleId = entity.getId();
        name = entity.getName();
        description = entity.getDescription();
    }
}
