package br.edu.utfpr.tsi.utfparking.domain.users.service;

import br.edu.utfpr.tsi.utfparking.application.exceptions.IlegalProcessDeleteException;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import br.edu.utfpr.tsi.utfparking.domain.users.factory.UserFactory;
import br.edu.utfpr.tsi.utfparking.structure.dtos.UserDTO;
import br.edu.utfpr.tsi.utfparking.structure.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    public static final long ID_USER = 1L;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFactory userFactory;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldDeleteUserById() {
        doNothing().when(userRepository).deleteById(eq(ID_USER));

        userService.delete(ID_USER);
        verify(userRepository, times(1)).deleteById(eq(ID_USER));
    }

    @Test
    void shouldReturnUserById() {
        when(userRepository.findById(eq(ID_USER))).thenReturn(Optional.of(createMockUser()));
        when(userFactory.createUserDTOByUser(any())).thenReturn(createMockUserDTO());

        UserDTO userDTO = userService.findById(ID_USER);

        assertNotNull(userDTO);
        assertEquals(ID_USER, userDTO.getId());
    }

    @Test
    void shouldReturnErrorWheUserNotExist() {
        when(userRepository.findById(eq(ID_USER))).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.findById(ID_USER));
    }

    private User createMockUser() {
        return User.builder()
                .id(ID_USER)
                .build();
    }

    private UserDTO createMockUserDTO() {
        return UserDTO.builder()
                .id(ID_USER)
                .build();
    }

}
