package br.edu.utfpr.tsi.utfparking.structure.repositories;

import br.edu.utfpr.tsi.utfparking.utils.CreateMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class AccessCardRepositoryTest {

    @Autowired
    private AccessCardRepository accessCardRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void shouldSaveAccessCard() {
        var roles = roleRepository.findAll();
        var accessCard = CreateMock.createAccessCard(null, roles, "username", "password");

        var result = accessCardRepository.save(accessCard);

        assertNotNull(result.getId());
        assertEquals(LocalDate.now(), result.getCreatedAt());
        assertEquals(LocalDate.now(), result.getUpdatedAt());
        assertEquals(accessCard.getUsername(), result.getUsername());
        assertEquals(accessCard.getUser(), result.getUser());
        assertEquals(accessCard.getRoles(), result.getRoles());
        assertEquals(accessCard.getAuthorities(), result.getAuthorities());
        assertEquals(accessCard.getPassword(), result.getPassword());
    }

}
