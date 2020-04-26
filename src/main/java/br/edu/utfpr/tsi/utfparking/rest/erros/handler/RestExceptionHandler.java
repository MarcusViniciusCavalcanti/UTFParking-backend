package br.edu.utfpr.tsi.utfparking.rest.erros.handler;

import br.edu.utfpr.tsi.utfparking.application.exceptions.IlegalProcessDeleteException;
import br.edu.utfpr.tsi.utfparking.domain.exceptions.UsernameExistException;
import br.edu.utfpr.tsi.utfparking.structure.dtos.ValidationErrors;
import br.edu.utfpr.tsi.utfparking.rest.erros.exceptions.IlegalRequestBodyException;
import br.edu.utfpr.tsi.utfparking.structure.dtos.ResponseError;
import br.edu.utfpr.tsi.utfparking.structure.exceptions.UpdateCarException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;
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
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .path(request.getServletPath())
                .build();

        return new ResponseEntity<>(errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(UpdateCarException.class)
    public ResponseEntity<?> handleUpdateCarException(UpdateCarException exception, HttpServletRequest request) {
        log.error("Error in process request: " + request.getRequestURL() + " cause by: " + exception.getClass().getSimpleName());
        var errors = ResponseError.builder()
                .title("Limite Update car exceeded")
                .timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .error(exception.getMessage())
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .path(request.getServletPath())
                .build();

        return new ResponseEntity<>(errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<?> handleUsernameExistException(UsernameExistException exception, HttpServletRequest request) {
        return buildResponseEntityNotFound(request, exception.getClass().getSimpleName(), "Username exist", exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handlerEntityNotFoundException(EntityNotFoundException exception, HttpServletRequest request) {
        return buildResponseEntityNotFound(request, exception.getClass().getSimpleName(), "Entity Not Found", exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<?> handlerEmptyResultDataAccessException(EmptyResultDataAccessException exception, HttpServletRequest request) {
        return buildResponseEntityNotFound(request, exception.getClass().getSimpleName(), "Entity Not Found", exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> buildResponseEntityNotFound(HttpServletRequest request, String simpleName, String s, String message, HttpStatus notFound) {
        log.error("Error in process request: " + request.getRequestURL() + " cause by: " + simpleName);

        var errors = ResponseError.builder()
                .title(s)
                .timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .error(message)
                .statusCode(notFound.value())
                .path(request.getServletPath())
                .build();

        return new ResponseEntity<>(errors, notFound);
    }
}
