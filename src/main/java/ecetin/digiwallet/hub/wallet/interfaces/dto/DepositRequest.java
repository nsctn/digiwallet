package ecetin.digiwallet.hub.wallet.interfaces.dto;

import ecetin.digiwallet.hub.wallet.domain.OppositePartyType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record DepositRequest(
    @NotNull(message = "Wallet ID is required")
    UUID walletId,
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    BigDecimal amount,
    
    @NotBlank(message = "Source is required")
    String source,
    
    @NotNull(message = "Source type is required")
    OppositePartyType sourceType
) {}