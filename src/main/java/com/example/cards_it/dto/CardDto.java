package com.example.cards_it.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@ToString(includeFieldNames = true)
public class CardDto {

    private Long id;

    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "^\\d{16}$", message = "Card number must be exactly 16 digits")
    private String cardNumber;

    @NotBlank(message = "Holder Name is required")
    private String holderName;

    @NotBlank(message = "Expiry date is required")
    @Pattern(
            regexp = "^(0[1-9]|1[0-2])\\/([0-9]{2})$",
            message = "Expiry date must be in MM/YY format (e.g., 12/28)"
    )
    private String expiryDate;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "ACTIVE|BLOCKED|INACTIVE", message = "Invalid status")
    private String status;

    private String lastTransactionId;
}