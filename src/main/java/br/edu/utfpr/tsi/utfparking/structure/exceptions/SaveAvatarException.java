package br.edu.utfpr.tsi.utfparking.structure.exceptions;

import java.io.IOException;

public class SaveAvatarException extends RuntimeException {
    public SaveAvatarException(String msg, IOException cause) {
        super(msg, cause);
    }
}
