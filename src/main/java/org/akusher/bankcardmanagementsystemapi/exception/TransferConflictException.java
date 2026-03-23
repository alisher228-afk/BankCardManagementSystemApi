package org.akusher.bankcardmanagementsystemapi.exception;

public class TransferConflictException extends RuntimeException {
    public TransferConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}

