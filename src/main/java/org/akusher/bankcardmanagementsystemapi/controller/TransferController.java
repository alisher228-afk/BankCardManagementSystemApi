package org.akusher.bankcardmanagementsystemapi.controller;


import jakarta.validation.Valid;
import org.akusher.bankcardmanagementsystemapi.dto.transfer.TransactionResponse;
import org.akusher.bankcardmanagementsystemapi.dto.transfer.TransferRequest;
import org.akusher.bankcardmanagementsystemapi.service.TransferService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {
    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransferRequest transferRequest) {
        transferService.transfer(
                transferRequest.fromId(),
                transferRequest.toId(),
                transferRequest.amount()
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/history/{accountId}")
    public ResponseEntity<Page<TransactionResponse>> getHistory(
            @PathVariable Long accountId,
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {

        return ResponseEntity.ok(transferService.getHistory(accountId,userDetails.getUsername(), pageable));
    }
}
