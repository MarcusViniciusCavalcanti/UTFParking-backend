package br.edu.utfpr.tsi.utfparking.domain.exceptions;

public class UsernameExistException extends RuntimeException {
    public UsernameExistException(String msg) {
        super(msg);
    }
}
