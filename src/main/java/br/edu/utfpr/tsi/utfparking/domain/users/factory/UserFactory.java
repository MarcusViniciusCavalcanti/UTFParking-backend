package br.edu.utfpr.tsi.utfparking.domain.users.factory;

import br.edu.utfpr.tsi.utfparking.domain.users.entity.TypeUser;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import br.edu.utfpr.tsi.utfparking.structure.dtos.*;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputUserDTO;
import org.springframework.stereotype.Component;


@Component
public class UserFactory {

    public User createUserByUserDTO(InputUserDTO dto) {
        return User.builder()
                .name(dto.getName())
                .typeUser(TypeUser.valueOf(dto.getType().name()))
                .build();
    }

    public UserDTO createUserDTOByUser(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .typeUser(TypeUserDTO.valueOf(user.getTypeUser().name()))
                .name(user.getName())
                .build();
    }

}
