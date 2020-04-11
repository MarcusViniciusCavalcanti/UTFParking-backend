package br.edu.utfpr.tsi.utfparking.domain.security.factory;

import br.edu.utfpr.tsi.utfparking.domain.security.entity.AccessCard;
import br.edu.utfpr.tsi.utfparking.domain.security.entity.Role;
import br.edu.utfpr.tsi.utfparking.structure.dtos.InputUserNewDTO;
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
        var inputUser = new InputUserNewDTO();

        inputUser.setAccountNonExpired(true);
        inputUser.setAccountNonLocked(true);
        inputUser.setEnabled(true);
        inputUser.setName(NAME_USER);
        inputUser.setUsername(USERNAME);
        inputUser.setPassword(PASSWORD);
        inputUser.setAuthorities(AUTHORITIES);
        inputUser.setId(ACCESS_CARD_ID);

        Role role = Role.builder()
                .id(2L)
                .name("role")
                .description("description")
                .build();

        AccessCard accessCardByInputUser = accessCardFactory.createAccessCardByInputUser(inputUser, List.of(role));

        assertEquals(accessCardByInputUser.getUsername(), USERNAME);
        assertEquals(accessCardByInputUser.getId(), ACCESS_CARD_ID);
        assertEquals(accessCardByInputUser.getRoles().size(), 1);
    }

}
