package br.edu.utfpr.tsi.utfparking.domain.security.service;

import br.edu.utfpr.tsi.utfparking.domain.security.entity.AccessCard;
import br.edu.utfpr.tsi.utfparking.domain.security.properties.JwtConfiguration;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenCreator {

    private final JwtConfiguration jwtConfiguration;

    public String encryptToken(SignedJWT signedJWT) throws JOSEException {
        log.info("Starting the encrypt token. . .");
        var payload = new Payload(signedJWT);
        var jweHeader = new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256)
                .contentType("JWT")
                .build();
        var jweObject = new JWEObject(jweHeader, payload);

        log.info("Encrypting token with system's private key");
        jweObject.encrypt(new DirectEncrypter(jwtConfiguration.getSecretKey().getBytes()));

        log.info("Token encrypted!");

        return jweObject.serialize();
    }

    @SneakyThrows
    public SignedJWT createSignedJWT(Authentication authentication, Date expiration) {
        log.info("Starting to create the signed JWS. . .");

        var principal = authentication.getPrincipal();
        var claims = createClaims(authentication, (AccessCard) principal, expiration);
        var keyPair = generateKayPair();

        log.info("Build JWK from the RSA Keys. . .");

        var jwk = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic()).keyID(UUID.randomUUID().toString()).build();
        var jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .jwk(jwk)
                .type(JOSEObjectType.JWT)
                .build();

        var signedJWT = new SignedJWT(jwsHeader, claims);

        log.info("Signing the token with private RSA key. . .");

        var signer = new RSASSASigner(keyPair.getPrivate());
        signedJWT.sign(signer);

        log.debug("Serialized token '{}'", signedJWT.serialize());

        return signedJWT;
    }

    private JWTClaimsSet createClaims(Authentication authentication, AccessCard accessCard, Date expiration) {
        log.info("Creating the ClaimsSet object to '{}'. . .", accessCard);


        var authorise = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JWTClaimsSet.Builder()
                .subject(accessCard.getUsername())
                .claim("authorities", authorise)
                .claim("id", accessCard.getId())
                .issuer("application web utfparking")
                .issueTime(new Date())
                .expirationTime(expiration)
                .build();
    }

    @SneakyThrows
    private KeyPair generateKayPair() {
        log.info("Creating RSA 2048. . .");

        var pairGenerator = KeyPairGenerator.getInstance("RSA");
        pairGenerator.initialize(2048);

        return pairGenerator.genKeyPair();
    }
}
