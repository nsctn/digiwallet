package ecetin.digiwallet.hub.wallet.interfaces.dto;

import ecetin.digiwallet.hub.wallet.domain.Status;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ApproveTransactionRequest(
    @NotNull(message = "Transaction ID is required")
    UUID transactionId,
    
    @NotNull(message = "Status is required")
    Status status
) {}