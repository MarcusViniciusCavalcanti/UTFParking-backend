package br.edu.utfpr.tsi.utfparking.domain.security.service;

import br.edu.utfpr.tsi.utfparking.domain.security.properties.JwtConfiguration;
import br.edu.utfpr.tsi.utfparking.structure.repositories.AccessCardRepository;
import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;
import java.text.ParseException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityContextUserServiceTest {

    @Mock
    private TokenConverter tokenConverter;
    @Mock
    private AccessCardRepository accessCardRepository;

    @Mock
    private JwtConfiguration jwtConfiguration;

    @InjectMocks
    private SecurityContextUserService securityContextUserService;

    @Test
    void shouldReturnExceptionWhenValidateToken() throws ParseException, JOSEException {
        when(tokenConverter.decryptToken(any())).thenThrow(new ParseException("message", 10));

        assertThrows(AccessDeniedException.class, () -> securityContextUserService.receiveTokenToSecurityHolder("token"));
    }

    @Test
    void shouldReturnExceptionWhenVaTokenValidate() throws ParseException, JOSEException, AccessDeniedException {
        when(tokenConverter.decryptToken(any())).thenReturn("token");
        doThrow(new AccessDeniedException("")).when(tokenConverter).validateTokenSignature(anyString());

        assertThrows(AccessDeniedException.class, () -> securityContextUserService.receiveTokenToSecurityHolder("token"));
    }
}
