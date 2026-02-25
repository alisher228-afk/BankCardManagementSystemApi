package org.akusher.bankcardmanagementsystemapi.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "cards",
        indexes = {
                @Index(name = "idx_cards_owner", columnList = "owner_id"),
                @Index(name = "idx_cards_status", columnList = "status"),
                @Index(name = "idx_cards_last4", columnList = "pan_last4")
        }
)
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "encrypted_pan", nullable = false, length = 512)
    private String encryptedPan;

    @NotNull
    @Column(name = "pan_last4", nullable = false, length = 4)
    private String panLast4;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @NotNull
    @Column(name = "expiry_date", nullable = false)
    private YearMonth expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus status = CardStatus.ACTIVE;

    @Version
    private Long version;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Card() {
    }

    public Card(String encryptedPan, String panLast4, YearMonth expiryDate) {
        this.encryptedPan = encryptedPan;
        this.panLast4 = panLast4;
        this.expiryDate = expiryDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEncryptedPan() {
        return encryptedPan;
    }

    public void setEncryptedPan(String encryptedPan) {
        this.encryptedPan = encryptedPan;
    }

    public String getPanLast4() {
        return panLast4;
    }

    public void setPanLast4(String panLast4) {
        this.panLast4 = panLast4;
    }


    public YearMonth getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(YearMonth expiryDate) {
        this.expiryDate = expiryDate;
    }

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

}