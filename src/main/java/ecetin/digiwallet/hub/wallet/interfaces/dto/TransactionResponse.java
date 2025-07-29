package ecetin.digiwallet.hub.wallet.interfaces.dto;

import ecetin.digiwallet.hub.wallet.domain.OppositePartyType;
import ecetin.digiwallet.hub.wallet.domain.Status;
import ecetin.digiwallet.hub.wallet.domain.Type;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
    UUID id,
    UUID walletId,
    BigDecimal amount,
    Type type,
    Status status,
    OppositePartyType oppositePartyType,
    String oppositeParty,
    Instant createdAt,
    Instant updatedAt
) {
    public static TransactionResponse fromTransaction(ecetin.digiwallet.hub.wallet.domain.Transaction transaction) {
        return new TransactionResponse(
            transaction.getId(),
            transaction.getWalletId(),
            transaction.getAmount(),
            transaction.getType(),
            transaction.getStatus(),
            transaction.getOppositePartyType(),
            transaction.getOppositeParty(),
            transaction.getCreatedAt(),
            transaction.getUpdatedAt()
        );
    }
}