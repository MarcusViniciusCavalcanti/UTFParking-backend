package br.edu.utfpr.tsi.utfparking.rest.erros.handler;

import br.edu.utfpr.tsi.utfparking.application.exceptions.IlegalProcessDeleteException;
import br.edu.utfpr.tsi.utfparking.application.exceptions.IlegalProcessNewUserException;
import br.edu.utfpr.tsi.utfparking.domain.exceptions.UsernameExistException;
import br.edu.utfpr.tsi.utfparking.structure.dtos.ValidationErrors;
import br.edu.utfpr.tsi.utfparking.rest.erros.exceptions.IlegalRequestBodyException;
import br.edu.utfpr.tsi.utfparking.structure.dtos.ResponseError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(IlegalProcessDeleteException.class)
    public ResponseEntity<ResponseError> deleteException(IlegalProcessDeleteException exception, HttpServletRequest request) {
        log.error("Error in process request: " + request.getRequestURL() + " cause by: " + exception.getClass().getSimpleName());

        var responseError = ResponseError.builder()
                .title(HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase())
                .timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .error(exception.getMessage())
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .path(request.getServletPath())
                .build();

        return ResponseEntity.unprocessableEntity()
                .body(responseError);
    }

    @ExceptionHandler(IlegalRequestBodyException.class)
    public ResponseEntity<?> handleIlegalRequestBodyException(IlegalRequestBodyException exception, HttpServletRequest request) {
        log.error("Error in process request: " + request.getRequestURL() + " cause by: " + exception.getClass().getSimpleName());
        var errors = ResponseError.builder()
                .title("Validation Errors " + exception.getTitle())
                .timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .error(new ValidationErrors(exception.errors()))
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .path(request.getServletPath())
                .build();

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<?> handleUsernameExistException(UsernameExistException exception, HttpServletRequest request) {
        log.error("Error in process request: " + request.getRequestURL() + " cause by: " + exception.getClass().getSimpleName());

        var errors = ResponseError.builder()
                .title("Username exist")
                .timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .error(exception.getMessage())
                .statusCode(HttpStatus.CONFLICT.value())
                .path(request.getServletPath())
                .build();

        return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IlegalProcessNewUserException.class)
    public ResponseEntity<?> handleUsernameExistException(IlegalProcessNewUserException exception, HttpServletRequest request) {
        log.error("Error in process request: " + request.getRequestURL() + " cause by: " + exception.getClass().getSimpleName());

        var errors = ResponseError.builder()
                .title("New user error")
                .timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .error(exception.getMessage())
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .path(request.getServletPath())
                .build();

        return new ResponseEntity<>(errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
