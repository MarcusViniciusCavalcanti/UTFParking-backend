package br.edu.utfpr.tsi.utfparking.domain.security.factory;

import br.edu.utfpr.tsi.utfparking.domain.security.entity.AccessCard;
import br.edu.utfpr.tsi.utfparking.domain.security.entity.Role;
import br.edu.utfpr.tsi.utfparking.structure.dtos.InputUserDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccessCardFactory {

    public AccessCard createAccessCardByInputUser(InputUserDTO dto, List<Role> roles) {
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
}
