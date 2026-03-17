package org.akusher.bankcardmanagementsystemapi.service;

import org.akusher.bankcardmanagementsystemapi.dto.TransactionResponse;
import org.akusher.bankcardmanagementsystemapi.dto.mapping.TransactionResponseMapping;
import org.akusher.bankcardmanagementsystemapi.entity.Account;
import org.akusher.bankcardmanagementsystemapi.entity.Transaction;
import org.akusher.bankcardmanagementsystemapi.entity.statusAndRole.AccountStatus;
import org.akusher.bankcardmanagementsystemapi.entity.statusAndRole.TransactionStatus;
import org.akusher.bankcardmanagementsystemapi.repository.AccountRepository;
import org.akusher.bankcardmanagementsystemapi.repository.TransactionRepository;
import org.akusher.bankcardmanagementsystemapi.service.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionResponseMapping transactionResponseMapping;

    public TransferService(AccountRepository accountRepository, TransactionRepository transactionRepository, TransactionResponseMapping transactionResponseMapping) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transactionResponseMapping = transactionResponseMapping;
    }
    @Transactional
    public void transfer(Long fromId, Long toId, BigDecimal amount) {

        validateBasicInput(fromId, toId, amount);

        Long firstLockId = Math.min(fromId, toId);
        Long secondLockId = Math.max(fromId, toId);

        Account first = accountRepository.findByIdForUpdate(firstLockId)
                .orElseThrow(() -> new AccountNotFoundException(firstLockId));

        Account second = accountRepository.findByIdForUpdate(secondLockId)
                .orElseThrow(() -> new AccountNotFoundException(secondLockId));

        Account from = fromId.equals(first.getId()) ? first : second;
        Account to = toId.equals(first.getId()) ? first : second;

        Transaction tx = buildTransaction(from, to, amount);
        tx.setStatus(TransactionStatus.PENDING);
        transactionRepository.save(tx);

        try {
            validateBusinessRules(from, to, amount);

            from.setBalance(from.getBalance().subtract(amount));
            to.setBalance(to.getBalance().add(amount));

            tx.setStatus(TransactionStatus.COMPLETED);

        } catch (RuntimeException ex) {
            tx.setStatus(TransactionStatus.FAILED);
            throw ex;
        }
    }

    private void validateBasicInput(Long fromId, Long toId, BigDecimal amount) {
        if (fromId == null || toId == null) {
            throw new InvalidTransferException("fromId and toId are required");
        }
        if (fromId.equals(toId)) {
            throw new InvalidTransferException("fromId and toId must be different");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferException("amount must be greater than zero");
        }
    }

    private void validateBusinessRules(Account from, Account to, BigDecimal amount) {
        if (from.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountInactiveException(from.getId());
        }
        if (to.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountInactiveException(to.getId());
        }
        if (from.getCurrency() == null || to.getCurrency() == null || !from.getCurrency().equalsIgnoreCase(to.getCurrency())) {
            throw new CurrencyMismatchException("Account currencies do not match");
        }
        if (from.getBalance() == null || from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds on account: " + from.getId());
        }
    }

    private Transaction buildTransaction(Account from, Account to, BigDecimal amount) {
        Transaction tx = new Transaction();
        tx.setFromAccount(from);
        tx.setToAccount(to);
        tx.setAmount(amount);
        tx.setCurrency(from.getCurrency());
        tx.setReference(java.util.UUID.randomUUID().toString());
        tx.setDescription("Transfer");
        return tx;
    }

    @Transactional
    public TransactionResponse deposit(Long accountId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferException("Amount must be greater than zero");
        }

        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountInactiveException(accountId);
        }

        Transaction tx = new Transaction();
        tx.setFromAccount(null);
        tx.setToAccount(account);
        tx.setAmount(amount);
        tx.setCurrency(account.getCurrency());
        tx.setStatus(TransactionStatus.PENDING);
        tx.setReference(java.util.UUID.randomUUID().toString());
        tx.setDescription("Deposit");
        transactionRepository.save(tx);

        try {
            account.setBalance(account.getBalance().add(amount));
            tx.setStatus(TransactionStatus.COMPLETED);
        } catch (RuntimeException ex) {
            tx.setStatus(TransactionStatus.FAILED);
            throw ex;
        }

        return transactionResponseMapping.mapToResponse(tx);
    }

}
