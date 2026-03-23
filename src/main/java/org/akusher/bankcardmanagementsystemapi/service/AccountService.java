package org.akusher.bankcardmanagementsystemapi.service;

import org.akusher.bankcardmanagementsystemapi.dto.account.AccountResponse;
import org.akusher.bankcardmanagementsystemapi.dto.mapping.AccountResponseMapping;
import org.akusher.bankcardmanagementsystemapi.entity.Account;
import org.akusher.bankcardmanagementsystemapi.entity.User;
import org.akusher.bankcardmanagementsystemapi.repository.AccountRepository;
import org.akusher.bankcardmanagementsystemapi.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountResponseMapping accountResponseMapping;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository,
                          AccountResponseMapping accountResponseMapping,
                          UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.accountResponseMapping = accountResponseMapping;
        this.userRepository = userRepository;
    }

        public AccountResponse createAccount(String username, String currency) {

        User user = getUserByUsername(username);

        Account account = new Account();
        account.setUser(user);
        account.setBalance(BigDecimal.ZERO);
        account.setCurrency(currency);
        account.setAccountNumber(generateAccountNumber());
        account.setIban(generateIban());

        Account savedAccount = accountRepository.save(account);

        return accountResponseMapping.mapToResponse(savedAccount);
    }

    private String generateIban() {
        return "KZ" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 18);
    }

    public Page<AccountResponse> getUserAccounts(String username, Pageable pageable) {

        User user = getUserByUsername(username);

        return accountRepository
                .findByUser(user, pageable)
                .map(accountResponseMapping::mapToResponse);
    }

    public AccountResponse getAccount(Long accountId, String username) {

        User user = getUserByUsername(username);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        return accountResponseMapping.mapToResponse(account);
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private String generateAccountNumber() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16);
    }
}