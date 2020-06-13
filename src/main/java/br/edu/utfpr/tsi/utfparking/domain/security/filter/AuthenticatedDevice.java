package br.edu.utfpr.tsi.utfparking.domain.security.filter;

import br.edu.utfpr.tsi.utfparking.domain.configuration.service.ApplicationConfigService;
import br.edu.utfpr.tsi.utfparking.structure.exceptions.DeviceDeniedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthenticatedDevice {

    private final ApplicationConfigService applicationConfigService;

    void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("Receiver recognize plate");
        var remoteAddr = request.getRemoteAddr();
        var ipAccepted = applicationConfigService.loadConfig().getIp();

        log.info("Check device authorization");
        if (remoteAddr.equals(ipAccepted)) {
            chain.doFilter(request, response);
        } else {
            throw new DeviceDeniedException(String.format("Device not authority with ip addressing: %s", remoteAddr));
        }
    }
}
