package br.edu.utfpr.tsi.utfparking.domain.security.service;

import br.edu.utfpr.tsi.utfparking.structure.repositories.AccessCardRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.nio.file.AccessDeniedException;
import java.text.ParseException;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityContextUserService {
    private final TokenConverter tokenConverter;

    private final AccessCardRepository accessCardRepository;

    @SneakyThrows
    public void receiveTokenToSecurityHolder(String token) {
        String decryptToken = decryptToken(token);
        SignedJWT signedJWT = validatingToken(decryptToken);
        setSecurityContext(signedJWT);
    }

    private String decryptToken(String encryptedToken) throws AccessDeniedException {
        try {
            return tokenConverter.decryptToken(encryptedToken);
        } catch (ParseException | JOSEException e) {
            throw new AccessDeniedException("Token is invalid");
        }
    }

    private SignedJWT validatingToken(String encryptedToken) throws AccessDeniedException {
        tokenConverter.validateTokenSignature(encryptedToken);
        try {
            return SignedJWT.parse(encryptedToken);
        } catch (ParseException e) {
            throw new AccessDeniedException("Token is invalid");
        }
    }

    private void setSecurityContext(SignedJWT signedJWT) {
        try {
            var jwtClaimsSet = signedJWT.getJWTClaimsSet();
            var subject = jwtClaimsSet.getSubject();

            if (subject == null) {
                throw new JOSEException("subject missing from JWT");
            }

            var id = jwtClaimsSet.getLongClaim("id");

            accessCardRepository.findById(id)
                    .ifPresentOrElse(
                            accessCard -> {
                                var auth = new UsernamePasswordAuthenticationToken(accessCard, null, accessCard.getAuthorities());
                                auth.setDetails(accessCard);
                                SecurityContextHolder.getContext().setAuthentication(auth);
                            },
                            () -> {
                                throw new EntityNotFoundException(String.format("Access card by username %s not found", subject));
                            }
                    );

        } catch (Exception ex) {
            log.error("Error setting security context {}", ex.getMessage());
            SecurityContextHolder.clearContext();
        }
    }
}
