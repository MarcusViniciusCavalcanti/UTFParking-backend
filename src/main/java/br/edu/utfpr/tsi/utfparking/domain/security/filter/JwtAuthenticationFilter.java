package br.edu.utfpr.tsi.utfparking.domain.security.filter;

import br.edu.utfpr.tsi.utfparking.domain.security.entity.AccessCard;
import br.edu.utfpr.tsi.utfparking.domain.security.properties.JwtConfiguration;
import br.edu.utfpr.tsi.utfparking.domain.security.service.TokenCreator;
import br.edu.utfpr.tsi.utfparking.structure.dtos.LoginDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final ObjectMapper objectMapper;

    private final JwtConfiguration jwtConfiguration;

    private final TokenCreator tokenCreator;

    @Override
    @SneakyThrows
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        log.info("Attempting authentication. . .");

        var credentials = Optional.ofNullable(objectMapper.readValue(request.getInputStream(), LoginDTO.class))
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("login input is empty"));

        log.info("Creating the authentication object for the access card: '{}'", credentials.getEmail());

        var token = new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword(), Collections.emptyList());

        var accessCard = AccessCard.builder()
                .username(credentials.getEmail())
                .password(credentials.getPassword())
                .build();

        token.setDetails(accessCard);

        return authenticationManager.authenticate(token);
    }

    @Override
    @SneakyThrows
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        log.info("Authentication was successful for the user '{}', generating JWE token", authResult.getName());

        var expirationJWT = jwtConfiguration.expirationTimeDate();
        var headerJWT = jwtConfiguration.getHeader();
        var signedJWT = tokenCreator.createSignedJWT(authResult, expirationJWT);
        var encryptToken = tokenCreator.encryptToken(signedJWT);

        var token = objectMapper.createObjectNode()
                .put("value", headerJWT.getPrefix() + encryptToken)
                .put("expiration", expirationJWT.getTime());

        response.addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        response.addHeader("Access-Control-Expose-Headers", String.format("XSRF-TOKEN, %s", headerJWT.getName()));
        response.addHeader(headerJWT.getName(), headerJWT.getPrefix() + encryptToken);
        response.getWriter().write(objectMapper.createObjectNode().setAll(token).toString());
    }

}
