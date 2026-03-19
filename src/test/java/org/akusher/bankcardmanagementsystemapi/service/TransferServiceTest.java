package org.akusher.bankcardmanagementsystemapi.service;

import org.akusher.bankcardmanagementsystemapi.dto.TransactionResponse;
import org.akusher.bankcardmanagementsystemapi.dto.mapping.TransactionResponseMapping;
import org.akusher.bankcardmanagementsystemapi.entity.Account;
import org.akusher.bankcardmanagementsystemapi.entity.Transaction;
import org.akusher.bankcardmanagementsystemapi.entity.User;
import org.akusher.bankcardmanagementsystemapi.entity.statusAndRole.AccountStatus;
import org.akusher.bankcardmanagementsystemapi.repository.AccountRepository;
import org.akusher.bankcardmanagementsystemapi.repository.TransactionRepository;
import org.akusher.bankcardmanagementsystemapi.service.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionResponseMapping transactionResponseMapping;

    @InjectMocks
    private TransferService transferService;

    private Account fromAccount;
    private Account toAccount;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("akusher");

        fromAccount = new Account();
        fromAccount.setId(1L);
        fromAccount.setBalance(new BigDecimal("1000.00"));
        fromAccount.setCurrency("USD");
        fromAccount.setStatus(AccountStatus.ACTIVE);
        fromAccount.setUser(user);

        toAccount = new Account();
        toAccount.setId(2L);
        toAccount.setBalance(new BigDecimal("500.00"));
        toAccount.setCurrency("USD");
        toAccount.setStatus(AccountStatus.ACTIVE);
        toAccount.setUser(user);
    }

    //TRANSFER

    @Test
    void transfer_success() {
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(toAccount));
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        transferService.transfer(1L, 2L, new BigDecimal("200.00"));

        assertEquals(new BigDecimal("800.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("700.00"), toAccount.getBalance());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void transfer_insufficientFunds_throwsException() {
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(toAccount));
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        assertThrows(InsufficientFundsException.class, () ->
                transferService.transfer(1L, 2L, new BigDecimal("9999.00"))
        );
    }

    @Test
    void transfer_sameAccount_throwsException() {
        assertThrows(InvalidTransferException.class, () ->
                transferService.transfer(1L, 1L, new BigDecimal("100.00"))
        );
    }

    @Test
    void transfer_currencyMismatch_throwsException() {
        toAccount.setCurrency("EUR");

        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(toAccount));
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        assertThrows(CurrencyMismatchException.class, () ->
                transferService.transfer(1L, 2L, new BigDecimal("100.00"))
        );
    }

    @Test
    void transfer_accountNotFound_throwsException() {
        when(accountRepository.findByIdForUpdate(any())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () ->
                transferService.transfer(1L, 2L, new BigDecimal("100.00"))
        );
    }

    @Test
    void transfer_fromAccountInactive_throwsException() {
        fromAccount.setStatus(AccountStatus.BLOCKED);

        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(toAccount));
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        assertThrows(AccountInactiveException.class, () ->
                transferService.transfer(1L, 2L, new BigDecimal("100.00"))
        );
    }

    //DEPOSIT

    @Test
    void deposit_success() {
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromAccount));
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(transactionResponseMapping.mapToResponse(any())).thenReturn(mock(TransactionResponse.class));

        TransactionResponse response = transferService.deposit(1L, new BigDecimal("300.00"));

        assertEquals(new BigDecimal("1300.00"), fromAccount.getBalance());
        assertNotNull(response);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void deposit_accountNotFound_throwsException() {
        when(accountRepository.findByIdForUpdate(any())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () ->
                transferService.deposit(1L, new BigDecimal("100.00"))
        );
    }

    @Test
    void deposit_accountInactive_throwsException() {
        fromAccount.setStatus(AccountStatus.BLOCKED);
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromAccount));

        assertThrows(AccountInactiveException.class, () ->
                transferService.deposit(1L, new BigDecimal("100.00"))
        );
    }

    @Test
    void deposit_zeroAmount_throwsException() {
        assertThrows(InvalidTransferException.class, () ->
                transferService.deposit(1L, BigDecimal.ZERO)
        );
    }

    //WITHDRAW

    @Test
    void withdraw_success() {
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromAccount));
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(transactionResponseMapping.mapToResponse(any())).thenReturn(mock(TransactionResponse.class));

        TransactionResponse response = transferService.withdraw(1L, new BigDecimal("200.00"), "akusher");

        assertEquals(new BigDecimal("800.00"), fromAccount.getBalance());
        assertNotNull(response);
    }

    @Test
    void withdraw_insufficientFunds_throwsException() {
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromAccount));

        assertThrows(InsufficientFundsException.class, () ->
                transferService.withdraw(1L, new BigDecimal("9999.00"), "akusher")
        );
    }

    @Test
    void withdraw_accessDenied_throwsException() {
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromAccount));

        assertThrows(AccessDeniedException.class, () ->
                transferService.withdraw(1L, new BigDecimal("100.00"), "hacker")
        );
    }

    @Test
    void withdraw_accountInactive_throwsException() {
        fromAccount.setStatus(AccountStatus.BLOCKED);
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromAccount));

        assertThrows(AccountInactiveException.class, () ->
                transferService.withdraw(1L, new BigDecimal("100.00"), "akusher")
        );
    }
}