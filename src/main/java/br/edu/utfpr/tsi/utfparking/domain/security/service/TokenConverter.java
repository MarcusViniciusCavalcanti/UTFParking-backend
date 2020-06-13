package br.edu.utfpr.tsi.utfparking.domain.security.service;

import br.edu.utfpr.tsi.utfparking.domain.security.properties.JwtConfiguration;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.text.ParseException;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenConverter {

    private final JwtConfiguration jwtConfiguration;

    public String decryptToken(String encryptedToken) throws ParseException, JOSEException {
        log.info("Decrypt token. . .");

        var jweObject = JWEObject.parse(encryptedToken);
        var directDecrypted = new DirectDecrypter(jwtConfiguration.getSecretKey().getBytes());
        jweObject.decrypt(directDecrypted);

        log.info("Token decrypted, returning signed token. . .");

        return jweObject.getPayload().toSignedJWT().serialize();
    }

    public void validateTokenSignature(String signedToken) throws AccessDeniedException {
        log.info("Starting method to validate toke signature");

        SignedJWT signedJWT = null;
        try {
            signedJWT = SignedJWT.parse(signedToken);

            log.info("Token parsed! Retrieving public key from signed token");

            var publicKey = RSAKey.parse(signedJWT.getHeader().getJWK().toJSONObject());

            log.info("Public key retrieved, validating signature. . .");

            if (!signedJWT.verify(new RSASSAVerifier(publicKey))) {
                throw new AccessDeniedException("Invalid token signature");
            }
        } catch (ParseException | AccessDeniedException | JOSEException e) {
            throw new AccessDeniedException("Token is invalid");
        }


        log.info("The token has a valid signature!");
    }
}
