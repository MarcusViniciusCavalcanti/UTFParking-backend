package br.edu.utfpr.tsi.utfparking.application.service.impl;

import br.edu.utfpr.tsi.utfparking.application.exceptions.IlegalProcessDeleteException;
import br.edu.utfpr.tsi.utfparking.application.service.UserApplicationService;
import br.edu.utfpr.tsi.utfparking.domain.users.service.UserService;
import br.edu.utfpr.tsi.utfparking.structure.dtos.UserDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserApplicationServiceImplTest {

    public static final long ID = 1L;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserApplicationServiceImpl userApplicationService;

    @Test
    void shouldReturnErrorWhenDeleteSelf() {
        Mockito.when(userService.getUserRequest()).thenReturn(UserDTO.builder().id(ID).build());

        assertThrows(IlegalProcessDeleteException.class, () -> userApplicationService.deleteById(ID));
    }
}
