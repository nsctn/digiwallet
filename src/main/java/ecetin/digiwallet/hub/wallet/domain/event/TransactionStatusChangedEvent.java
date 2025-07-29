package ecetin.digiwallet.hub.wallet.domain.event;

import ecetin.digiwallet.hub.wallet.domain.OppositePartyType;
import ecetin.digiwallet.hub.wallet.domain.Status;
import ecetin.digiwallet.hub.wallet.domain.Type;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionStatusChangedEvent(
    UUID id,
    UUID walletId,
    Status newStatus,
    Type type,
    BigDecimal amount,
    OppositePartyType oppositePartyType,
    String oppositeParty) {}
