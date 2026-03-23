package org.akusher.bankcardmanagementsystemapi.exception;

public class AccountInactiveException extends RuntimeException {
    public AccountInactiveException(Long id) {
        super("Account is not active: " + id);
    }
}

