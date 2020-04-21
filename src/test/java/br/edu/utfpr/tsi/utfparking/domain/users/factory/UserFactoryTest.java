package br.edu.utfpr.tsi.utfparking.domain.users.factory;

import br.edu.utfpr.tsi.utfparking.domain.users.entity.TypeUser;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputUserDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.TypeUserDTO;
import br.edu.utfpr.tsi.utfparking.utils.CreateMock;
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
        var fulanoAdmin = createMockInputUserDTO(List.of(1L, 2L), "fulano_admin");
        var userByUserDTO = userFactory.createUserByUserDTO(fulanoAdmin);

        assertEquals(fulanoAdmin.getName(), userByUserDTO.getName());
    }

    @Test
    void shouldHaveCreateUserDTOByUser() {
        var mockUser = createMockUser();
        var userDTOByUser = userFactory.createUserDTOByUser(mockUser);

        assertEquals(mockUser.getName(), userDTOByUser.getName());
        assertEquals(mockUser.getTypeUser().name(), userDTOByUser.getTypeUser().name());
    }

    private InputUserDTO createMockInputUserDTO(List<Long> roles, String username) {
        return CreateMock.createMockInputUserDTO(NAME_USER, username, PASSWORD, TypeUserDTO.SERVICE, roles);
    }

    private User createMockUser() {
        var role = CreateMock.createRole(1L, "description", "name");
        var accessCard = CreateMock.createAccessCard(1L, List.of(role), USERNAME, PASSWORD);

        return CreateMock.createUser(ID_USER, accessCard, TypeUser.SERVICE, NAME_USER, null);
    }

}
