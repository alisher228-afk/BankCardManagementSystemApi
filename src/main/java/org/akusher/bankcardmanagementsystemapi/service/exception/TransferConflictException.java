package org.akusher.bankcardmanagementsystemapi.service.exception;

public class TransferConflictException extends RuntimeException {
    public TransferConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}

