package org.akusher.bankcardmanagementsystemapi.controller;

import jakarta.validation.Valid;
import org.akusher.bankcardmanagementsystemapi.dto.*;
import org.akusher.bankcardmanagementsystemapi.service.AccountService;
import org.akusher.bankcardmanagementsystemapi.service.TransferService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")

public class AccountController {

    private final AccountService accountService;
    private final TransferService transferService;

    public AccountController(AccountService accountService, TransferService transferService) {
        this.accountService = accountService;
        this.transferService = transferService;
    }

    @PostMapping
    public AccountResponse createAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateAccountRequest request
    ) {
        return accountService.createAccount(
                userDetails.getUsername(),
                request.currency()
        );
    }

    @GetMapping
    public Page<AccountResponse> getMyAccounts(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable
    ) {
        return accountService.getUserAccounts(
                userDetails.getUsername(),
                pageable
        );
    }

    @GetMapping("/{id}")
    public AccountResponse getAccount(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return accountService.getAccount(
                id,
                userDetails.getUsername()
        );
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @PathVariable Long accountId,
            @Valid @RequestBody DepositRequest request) {

        TransactionResponse response = transferService.deposit(accountId, request.amount());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            @PathVariable Long accountId,
            @Valid @RequestBody WithdrawRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        TransactionResponse response = transferService.withdraw(
                accountId,
                request.amount(),
                userDetails.getUsername()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}