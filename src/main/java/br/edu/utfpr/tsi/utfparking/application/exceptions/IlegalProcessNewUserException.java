package br.edu.utfpr.tsi.utfparking.application.exceptions;

public class IlegalProcessNewUserException extends RuntimeException {

    public IlegalProcessNewUserException(Throwable exception) {
        super(exception);
    }
    
}
