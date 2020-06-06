package br.edu.utfpr.tsi.utfparking.domain.security.filter;

import br.edu.utfpr.tsi.utfparking.domain.security.properties.JwtConfiguration;
import br.edu.utfpr.tsi.utfparking.domain.security.service.SecurityContextUserService;
import br.edu.utfpr.tsi.utfparking.domain.security.service.TokenConverter;
import br.edu.utfpr.tsi.utfparking.structure.repositories.AccessCardRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.persistence.EntityNotFoundException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final JwtConfiguration jwtConfiguration;

    private final SecurityContextUserService securityContextUserService;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
                                  JwtConfiguration jwtConfiguration,
                                  SecurityContextUserService securityContextUserService) {
        super(authenticationManager);
        this.jwtConfiguration = jwtConfiguration;
        this.securityContextUserService = securityContextUserService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        log.info("Running request from id address '{}' ", httpServletRequest.getRemoteAddr());
        String header = httpServletRequest.getHeader(jwtConfiguration.getHeader().getName());

        if (header == null || !header.startsWith(jwtConfiguration.getHeader().getPrefix())) {
            log.info("Running request not authenticate ");
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        log.info("Extract to token from request header. . .");
        var token = header.replace(jwtConfiguration.getHeader().getPrefix(), "").trim();

        log.info("Decrypted token and validate sign. . .");

        securityContextUserService.receiveTokenToSecurityHolder(token);
        log.info("Token validate authenticated successfully!");
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
