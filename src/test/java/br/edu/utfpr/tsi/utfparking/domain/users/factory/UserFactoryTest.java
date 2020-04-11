package br.edu.utfpr.tsi.utfparking.domain.users.factory;

import br.edu.utfpr.tsi.utfparking.domain.security.entity.AccessCard;
import br.edu.utfpr.tsi.utfparking.domain.security.entity.Role;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.TypeUser;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import br.edu.utfpr.tsi.utfparking.structure.dtos.InputUserNewDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.TypeUserDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = UserFactory.class)
class UserFactoryTest {
    private static final long ID_USER = 1L;
    private static final String NAME_USER = "Name";
    private static final String PASSWORD = "12345678";
    private static final String USERNAME = "Username";

    @Autowired
    private UserFactory userFactory;

    @Test
    void shouldHaveCreateUserByInputUser() {
        InputUserNewDTO fulanoAdmin = createMockInputUserDTO(List.of(1L, 2L), "fulano_admin");

        User userByUserDTO = userFactory.createUserByUserDTO(fulanoAdmin);

        assertEquals(fulanoAdmin.getName(), userByUserDTO.getName());
    }

    @Test
    void shouldHaveCreateUserDTOByUser() {
        User mockUser = createMockUser();

        UserDTO userDTOByUser = userFactory.createUserDTOByUser(mockUser);

        assertEquals(mockUser.getName(), userDTOByUser.getName());
        assertEquals(mockUser.getTypeUser().name(), userDTOByUser.getTypeUser().name());
    }

    private InputUserNewDTO createMockInputUserDTO(List<Long> roles, String username) {
        var inputUser = new InputUserNewDTO();

        inputUser.setAccountNonExpired(true);
        inputUser.setAccountNonLocked(true);
        inputUser.setEnabled(true);
        inputUser.setName(NAME_USER);
        inputUser.setUsername(username);
        inputUser.setType(TypeUserDTO.SERVICE);
        inputUser.setPassword(PASSWORD);
        inputUser.setAuthorities(roles);

        return inputUser;
    }

    private User createMockUser() {
        User user = User.builder()
                .typeUser(TypeUser.SERVICE)
                .id(ID_USER)
                .build();

        Role role = Role.builder()
                .id(1L)
                .description("description")
                .name("name")
                .build();

        AccessCard accessCard = AccessCard.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .roles(List.of(role))
                .build();

        user.setAccessCard(accessCard);
        return user;
    }

}
