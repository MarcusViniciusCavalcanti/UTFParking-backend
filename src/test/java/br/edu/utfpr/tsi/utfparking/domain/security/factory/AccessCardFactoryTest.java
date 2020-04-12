package br.edu.utfpr.tsi.utfparking.domain.security.factory;

import br.edu.utfpr.tsi.utfparking.structure.dtos.TypeUserDTO;
import br.edu.utfpr.tsi.utfparking.utils.CreateMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = AccessCardFactory.class)
class AccessCardFactoryTest {
    private static final String NAME_USER = "Name";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final List<Long> AUTHORITIES = List.of(1L);
    private static final long ACCESS_CARD_ID = 1L;


    @Autowired
    private AccessCardFactory accessCardFactory;

    @Test
    void shouldHaveCreateAccessCardByInputUserDTO() {
        var inputUser = CreateMock.createMockInputUserDTO(NAME_USER, USERNAME, PASSWORD, TypeUserDTO.SERVICE, AUTHORITIES);
        var role = CreateMock.createRole(2L, "description", "role");

        var accessCardByInputUser = accessCardFactory.createAccessCardByInputUser(inputUser, List.of(role));

        assertEquals(accessCardByInputUser.getUsername(), USERNAME);
        assertEquals(accessCardByInputUser.getRoles().size(), 1);
    }

}
