package org.akusher.bankcardmanagementsystemapi.controller;

import org.akusher.bankcardmanagementsystemapi.dto.AccountResponse;
import org.akusher.bankcardmanagementsystemapi.dto.CreateAccountRequest;
import org.akusher.bankcardmanagementsystemapi.entity.User;
import org.akusher.bankcardmanagementsystemapi.service.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")

public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
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
}