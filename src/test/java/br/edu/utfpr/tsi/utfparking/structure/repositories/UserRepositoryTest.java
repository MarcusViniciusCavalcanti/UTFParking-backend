package br.edu.utfpr.tsi.utfparking.structure.repositories;

import br.edu.utfpr.tsi.utfparking.domain.security.entity.AccessCard;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "123456789";

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldReturnTrueWhenUserExist() {
        AccessCard accessCard = AccessCard.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .build();

        User user = User.builder()
                .name("Name")
                .accessCard(accessCard)
                .build();

        userRepository.saveAndFlush(user);

        assertTrue(userRepository.existsUserByAccessCardUsername(USERNAME));
    }

    @Test
    void shouldReturnFalseWheUserNotExit() {
        assertFalse(userRepository.existsUserByAccessCardUsername("other_username"));
    }
}
