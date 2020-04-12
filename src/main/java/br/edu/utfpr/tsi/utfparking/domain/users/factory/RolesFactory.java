package br.edu.utfpr.tsi.utfparking.domain.users.factory;

import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import br.edu.utfpr.tsi.utfparking.structure.dtos.RoleDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RolesFactory {

    public List<RoleDTO> createRoleDTOS(User user) {
        return user.getAccessCard().getRoles().stream()
                .map(role -> RoleDTO.builder()
                        .id(role.getId())
                        .description(role.getDescription())
                        .name(role.getName())
                        .build()
                ).collect(Collectors.toList());
    }
}
