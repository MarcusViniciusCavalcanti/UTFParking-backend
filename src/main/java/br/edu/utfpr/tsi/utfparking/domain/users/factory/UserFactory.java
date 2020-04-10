package br.edu.utfpr.tsi.utfparking.domain.users.factory;

import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import br.edu.utfpr.tsi.utfparking.structure.dtos.AccessCardDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.InputUserDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.RoleDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserFactory {

    public User createUserByUserDTO(InputUserDTO dto) {
        return User.builder()
                .name(dto.getName())
                .build();
    }

    public UserDTO createUserDTOByUser(User user) {
        var accessCard = user.getAccessCard();

        var rolesDto = accessCard.getRoles().stream()
                .map(role -> RoleDTO.builder()
                        .id(role.getId())
                        .description(role.getDescription())
                        .name(role.getName())
                        .build())
                .collect(Collectors.toList());

        var accessCardDto = AccessCardDTO.builder()
                .username(accessCard.getUsername())
                .accountNonExpired(accessCard.isAccountNonExpired())
                .accountNonLocked(accessCard.isAccountNonLocked())
                .credentialsNonExpired(accessCard.isCredentialsNonExpired())
                .enabled(accessCard.isEnabled())
                .roles(rolesDto)
                .build();

        return UserDTO.builder()
                .accessCard(accessCardDto)
                .id(user.getId())
                .name(user.getName())
                .build();
    }

}
