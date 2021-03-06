package br.edu.utfpr.tsi.utfparking.domain.security.filter;

import br.edu.utfpr.tsi.utfparking.domain.security.properties.JwtConfiguration;
import br.edu.utfpr.tsi.utfparking.domain.security.service.SecurityContextUserService;
import br.edu.utfpr.tsi.utfparking.structure.exceptions.DeviceDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private static final String SEND_PLATE = "/api/v1/recognizers/send/plate";

    private final JwtConfiguration jwtConfiguration;

    private final SecurityContextUserService securityContextUserService;

    private final AuthenticatedDevice authenticatedDevice;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
                                  JwtConfiguration jwtConfiguration,
                                  SecurityContextUserService securityContextUserService,
                                  AuthenticatedDevice authenticatedDevice) {
        super(authenticationManager);
        this.jwtConfiguration = jwtConfiguration;
        this.securityContextUserService = securityContextUserService;
        this.authenticatedDevice = authenticatedDevice;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("Running request from id address '{}' ", httpServletRequest.getRemoteAddr());
        String header = httpServletRequest.getHeader(jwtConfiguration.getHeader().getName());

        if (header == null || !header.startsWith(jwtConfiguration.getHeader().getPrefix())) {
            log.info("Running request not authenticate ");
            try {
                checkRequest(httpServletRequest, httpServletResponse, filterChain);
            } catch (ServletException e) {
                throw new DeviceDeniedException("Device not authority with ip addressing");
            }
            return;
        }

        log.info("Extract to token from request header. . .");
        var token = header.replace(jwtConfiguration.getHeader().getPrefix(), "").trim();

        log.info("Decrypted token and validate sign. . .");

        securityContextUserService.receiveTokenToSecurityHolder(token);
        log.info("Token validate authenticated successfully!");
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void checkRequest(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        var uri = request.getRequestURI();
        if (uri.equals(SEND_PLATE)) {
            authenticatedDevice.doFilterInternal(request, response, filterChain);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
