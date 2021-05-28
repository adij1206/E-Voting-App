package com.aditya.onlinevoting.Utils;

public class QrCodeException extends Exception {
    public QrCodeException(String message) {
        super(message);
    }

    public QrCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public QrCodeException(Throwable cause) {
        super(cause);
    }
}
