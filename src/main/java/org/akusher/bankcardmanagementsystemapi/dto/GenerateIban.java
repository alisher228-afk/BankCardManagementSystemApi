package org.akusher.bankcardmanagementsystemapi.dto;

import java.util.UUID;

public class GenerateIban {
    public String generateIban() {
        return "KZ" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 18);
    }
}
