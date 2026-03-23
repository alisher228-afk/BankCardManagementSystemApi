package org.akusher.bankcardmanagementsystemapi.dto.mapping;

import org.akusher.bankcardmanagementsystemapi.dto.account.AccountResponse;
import org.akusher.bankcardmanagementsystemapi.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountResponseMapping {
    public AccountResponse mapToResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance()
        );
    }
}
