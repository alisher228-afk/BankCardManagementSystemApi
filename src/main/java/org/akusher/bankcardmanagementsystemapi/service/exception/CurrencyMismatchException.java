package org.akusher.bankcardmanagementsystemapi.service.exception;

public class CurrencyMismatchException extends RuntimeException {
    public CurrencyMismatchException(String message) {
        super(message);
    }
}

