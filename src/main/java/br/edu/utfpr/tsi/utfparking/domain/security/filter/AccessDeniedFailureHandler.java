package br.edu.utfpr.tsi.utfparking.domain.security.filter;

import br.edu.utfpr.tsi.utfparking.structure.dtos.ResponseError;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
public class AccessDeniedFailureHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException {
        log.error("Error in process request: " + request.getRequestURL() + " cause by: " + exception.getClass().getSimpleName());

        var responseError = ResponseError.builder()
                .title(HttpStatus.FORBIDDEN.getReasonPhrase())
                .timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .error("Access resource denied.")
                .statusCode(HttpStatus.FORBIDDEN.value())
                .path(request.getServletPath())
                .build();

        var objectMapper = new ObjectMapper();

        response.getWriter().println(objectMapper.writeValueAsString(responseError));
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
}
