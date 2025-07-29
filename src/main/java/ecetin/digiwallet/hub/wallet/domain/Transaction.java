package ecetin.digiwallet.hub.wallet.domain;

import ecetin.digiwallet.hub.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Entity
@Table(name = "TRANSACTION")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction extends BaseEntity<UUID> {

    @Column(nullable = false)
    private UUID walletId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "opposite_party_type", nullable = false)
    private OppositePartyType oppositePartyType;

    @Column(name = "opposite_party", nullable = false)
    private String oppositeParty;

    private Transaction(UUID id, UUID walletId, BigDecimal amount,
                        Type type, Status status,
                        OppositePartyType oppositePartyType,
                        String oppositeParty) {
        this.setId(id);
        this.walletId = walletId;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.oppositePartyType = oppositePartyType;
        this.oppositeParty = oppositeParty;
    }

    public static Transaction deposit(UUID walletId, BigDecimal amount, String source,
                                      OppositePartyType partyType, Status status) {
        return new Transaction(UUID.randomUUID(), walletId, amount,
                Type.DEPOSIT, status, partyType, source);
    }

    public static Transaction withdraw(UUID walletId, BigDecimal amount, String destination,
                                       OppositePartyType partyType, Status status) {
        return new Transaction(UUID.randomUUID(), walletId, amount,
                Type.WITHDRAW, status, partyType, destination);
    }

    public void setStatus(Status newStatus) {
        if (this.status != Status.PENDING) {
            throw new IllegalStateException("Only PENDING transactions can be updated");
        }
        this.status = newStatus;
    }
}
