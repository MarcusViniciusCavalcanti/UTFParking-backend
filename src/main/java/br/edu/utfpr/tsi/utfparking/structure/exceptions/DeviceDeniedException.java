package br.edu.utfpr.tsi.utfparking.structure.exceptions;



public class DeviceDeniedException extends RuntimeException {

    public DeviceDeniedException(String msg) {
        super(msg);
    }
}
