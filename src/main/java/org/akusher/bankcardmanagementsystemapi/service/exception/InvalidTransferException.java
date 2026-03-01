package org.akusher.bankcardmanagementsystemapi.service.exception;

public class InvalidTransferException extends RuntimeException {
    public InvalidTransferException(String message) {
        super(message);
    }
}

