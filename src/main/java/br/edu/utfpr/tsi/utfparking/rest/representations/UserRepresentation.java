package br.edu.utfpr.tsi.utfparking.rest.representations;

import br.edu.utfpr.tsi.utfparking.structure.dtos.UserDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserRepresentation extends RepresentationModel<UserRepresentation> {

    private Long userId;

    private String name;

    private List<RoleRepresentation> roles;

    public void copyAttributeBy(UserDTO user) {
        this.userId = user.getId();
        this.name = user.getName();
        this.roles = user.getAccessCard().getRoles().stream()
            .map(role -> new RoleRepresentation(role.getId(), role.getName(), role.getDescription()))
            .collect(Collectors.toList());

    }
}
