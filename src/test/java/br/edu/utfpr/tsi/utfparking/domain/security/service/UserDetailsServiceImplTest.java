package br.edu.utfpr.tsi.utfparking.domain.security.service;

import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import br.edu.utfpr.tsi.utfparking.structure.repositories.AccessCardRepository;
import br.edu.utfpr.tsi.utfparking.utils.CreateMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    private static final String BELTRANO_ADMIN = "vinicius_usuario";

    private static final String MESSAGE_ERROR = "this username: 'vinicius_usuario' not found";

    @Mock
    private AccessCardRepository accessCardRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void shouldReturnUser() {
        var userWithAccessCardUser = CreateMock.createUserWithAccessCardUser();
        var userDefaultUser = CreateMock.createUserDefaultUser();

        userWithAccessCardUser.setUser(userDefaultUser);

        when(accessCardRepository.findByUsername(eq(userWithAccessCardUser.getUsername()))).thenReturn(Optional.of(userWithAccessCardUser));

        var userDetails = userDetailsService.loadUserByUsername(userWithAccessCardUser.getUsername());

        assertEquals(BELTRANO_ADMIN, userDetails.getUsername());
    }

    @Test
    void shouldReturnExceptionWhenUserNotFound() {
        when(accessCardRepository.findByUsername(BELTRANO_ADMIN)).thenReturn(Optional.empty());

        var usernameNotFoundException = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(BELTRANO_ADMIN));

        assertEquals(MESSAGE_ERROR, usernameNotFoundException.getMessage());

    }
}
