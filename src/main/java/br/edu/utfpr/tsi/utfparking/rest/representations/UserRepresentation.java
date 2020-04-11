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

    private String type;

    private List<RoleRepresentation> roles;

    private CarRepresentation car;

    public void copyAttributeBy(UserDTO user) {
        this.userId = user.getId();
        this.name = user.getName();
        this.type = user.getTypeUser().name();
        this.car = new CarRepresentation();

        this.car.copyAttributeBy(user.getCar());

        this.roles = user.getAccessCard().getRoles().stream()
            .map(role -> new RoleRepresentation(role.getId(), role.getName(), role.getDescription()))
            .collect(Collectors.toList());

    }
}
