package br.edu.utfpr.tsi.utfparking.rest.representations;

import br.edu.utfpr.tsi.utfparking.rest.endpoints.UsersController;
import br.edu.utfpr.tsi.utfparking.structure.dtos.UserDTO;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Data
public class UserRepresentation extends RepresentationModel<UserRepresentation> {

    private Long userId;

    private String name;

    private String type;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

    private boolean credentialsNonExpired;

    private boolean enabled;

    private List<RoleRepresentation> roles;

    private CarRepresentation car;

    public void copyAttributeBy(UserDTO user) {
        userId = user.getId();
        name = user.getName();
        type = user.getTypeUser().name();
        accountNonExpired = user.getAccessCard().isAccountNonExpired();
        accountNonLocked = user.getAccessCard().isAccountNonLocked();
        credentialsNonExpired = user.getAccessCard().isCredentialsNonExpired();
        enabled = user.getAccessCard().isEnabled();

        user.car().ifPresent(carDTO -> {
            car = new CarRepresentation();
            car.copyAttributeBy(carDTO);
        });

        roles = user.getAccessCard().getRoles().stream()
            .map(role -> new RoleRepresentation(role.getId(), role.getName(), role.getDescription()))
            .collect(Collectors.toList());

        add(linkTo(methodOn(UsersController.class).downloadAvatar(userId)).withRel("avatar"));
        add(linkTo(methodOn(UsersController.class).getUserByAccessCard()).withRel("me"));
        add(linkTo(methodOn(UsersController.class).changeCar(userId, null, null)).withRel("updateCar"));
    }
}
