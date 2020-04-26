package br.edu.utfpr.tsi.utfparking.domain.security.filter;

import br.edu.utfpr.tsi.utfparking.domain.security.entity.AccessCard;
import br.edu.utfpr.tsi.utfparking.domain.security.properties.JwtConfiguration;
import br.edu.utfpr.tsi.utfparking.domain.security.service.TokenCreator;
import br.edu.utfpr.tsi.utfparking.structure.dtos.LoginDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.ResponseJWEDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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

        log.info("Creating the authentication object for the access card: '{}'", credentials.getUsername());

        var token = new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword(), Collections.emptyList());

        var accessCard = AccessCard.builder()
                .username(credentials.getUsername())
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

        var token = ResponseJWEDTO.builder()
                .value(headerJWT.getPrefix() + encryptToken)
                .expiration(expirationJWT.getTime())
                .build();

        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, String.format("XSRF-TOKEN, %s", headerJWT.getName()));
        response.addHeader(headerJWT.getName(), headerJWT.getPrefix() + encryptToken);
        response.getWriter().write(objectMapper.writeValueAsString(token));
    }

}
