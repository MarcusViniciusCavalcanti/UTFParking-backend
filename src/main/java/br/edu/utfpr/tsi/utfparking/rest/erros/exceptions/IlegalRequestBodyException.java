package br.edu.utfpr.tsi.utfparking.rest.erros.exceptions;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Collections;
import java.util.List;

public class IlegalRequestBodyException extends RuntimeException {
    private String title;
    private BindingResult resultSet;

    public IlegalRequestBodyException(String invalidBody, BindingResult resultSet) {
        super(invalidBody);
        title = invalidBody;
        this.resultSet = resultSet;
    }

    public List<FieldError> errors() {
        if (resultSet.hasErrors()) {
            return resultSet.getFieldErrors();
        }
        return Collections.emptyList();
    }

    public String getTitle() {
        return this.title;
    }
}
