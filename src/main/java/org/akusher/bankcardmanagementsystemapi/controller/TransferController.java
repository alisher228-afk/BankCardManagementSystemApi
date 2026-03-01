package org.akusher.bankcardmanagementsystemapi.controller;


import jakarta.validation.Valid;
import org.akusher.bankcardmanagementsystemapi.dto.TransferRequest;
import org.akusher.bankcardmanagementsystemapi.service.TransferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
