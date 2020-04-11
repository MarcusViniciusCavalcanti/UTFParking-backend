package br.edu.utfpr.tsi.utfparking.domain.security.factory;

import br.edu.utfpr.tsi.utfparking.domain.security.entity.AccessCard;
import br.edu.utfpr.tsi.utfparking.domain.security.entity.Role;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import br.edu.utfpr.tsi.utfparking.structure.dtos.AccessCardDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.InputUserNewDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.RoleDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.UserDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccessCardFactory {

    public AccessCard createAccessCardByInputUser(InputUserNewDTO dto, List<Role> roles) {
        return AccessCard.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .roles(roles)
                .accountNonExpired(dto.isAccountNonExpired())
                .credentialsNonExpired(dto.isCredentialsNonExpired())
                .enabled(dto.isEnabled())
                .accountNonLocked(dto.isAccountNonLocked())
                .build();
    }

    public AccessCardDTO createAccessCardByUserDTO(UserDTO userDTO, AccessCard accessCard, List<RoleDTO> roles) {
        return AccessCardDTO.builder()
                .username(accessCard.getUsername())
                .accountNonExpired(accessCard.isAccountNonExpired())
                .accountNonLocked(accessCard.isAccountNonLocked())
                .credentialsNonExpired(accessCard.isCredentialsNonExpired())
                .enabled(accessCard.isEnabled())
                .roles(roles)
                .build();
    }

    public AccessCardDTO createAccessCardByUserDTO(User newUser) {
        var accessCard = newUser.getAccessCard();

        var rolesDto = accessCard.getRoles().stream()
                .map(role -> RoleDTO.builder()
                        .id(role.getId())
                        .description(role.getDescription())
                        .name(role.getName())
                        .build())
                .collect(Collectors.toList());

        return AccessCardDTO.builder()
                .username(accessCard.getUsername())
                .accountNonExpired(accessCard.isAccountNonExpired())
                .accountNonLocked(accessCard.isAccountNonLocked())
                .credentialsNonExpired(accessCard.isCredentialsNonExpired())
                .enabled(accessCard.isEnabled())
                .roles(rolesDto)
                .build();
    }
}
