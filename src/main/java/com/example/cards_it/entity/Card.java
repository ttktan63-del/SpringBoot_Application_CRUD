package com.example.cards_it.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cards")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="card_number", nullable = false, unique = true)
    private String cardNumber;

    @Column(name="holder_name", nullable = false)
    private String holderName;

    @Column(name="expiry_date")
    private String expiryDate;

    @Column(name="status")
    private String status;

    @Column(name="last_trxn_id")
    private String lastTransactionId;
}