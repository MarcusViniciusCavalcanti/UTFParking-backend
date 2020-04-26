package br.edu.utfpr.tsi.utfparking.structure.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void shouldReturnDefaultRoles() {
        var roles = roleRepository.findAll();

        assertEquals(3, roles.size());
    }
}
