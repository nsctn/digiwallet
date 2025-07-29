package ecetin.digiwallet.hub.wallet.domain;

import ecetin.digiwallet.hub.common.model.BaseAggregateRoot;
import ecetin.digiwallet.hub.wallet.domain.event.TransactionCreatedEvent;
import ecetin.digiwallet.hub.wallet.domain.event.TransactionStatusChangedEvent;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "WALLET")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wallet extends BaseAggregateRoot<UUID> {

  @Column(nullable = false)
  private UUID customerId;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Currency currency;

  @Column(name = "active_for_shopping", nullable = false)
  private boolean activeForShopping;

  @Column(name = "active_for_withdraw", nullable = false)
  private boolean activeForWithdraw;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal balance;

  @Column(name = "usable_balance", nullable = false, precision = 19, scale = 4)
  private BigDecimal usableBalance;

  public Wallet(UUID id, UUID customerId, String name, String currency) {
    this(id, customerId, name, currency, true, true);
  }
  
  public Wallet(UUID id, UUID customerId, String name, String currency, boolean activeForShopping, boolean activeForWithdraw) {
    if (customerId == null) throw new IllegalArgumentException("Customer ID is required");
    this.setId(id);
    this.customerId = customerId;
    this.name = name;
    this.currency = new Currency(currency);
    this.balance = BigDecimal.ZERO;
    this.usableBalance = BigDecimal.ZERO;
    this.activeForShopping = activeForShopping;
    this.activeForWithdraw = activeForWithdraw;
  }

  public Transaction deposit(BigDecimal amount, String source, OppositePartyType partyType) {
    Status status =
        amount.compareTo(BigDecimal.valueOf(1000)) > 0 ? Status.PENDING : Status.APPROVED;

    Transaction transaction = Transaction.deposit(this.getId(), amount, source, partyType, status);

    balance = balance.add(amount);
    if (status == Status.APPROVED) {
      usableBalance = usableBalance.add(amount);
    }

    this.registerEvent(
        new TransactionCreatedEvent(
            transaction.getId(),
            transaction.getWalletId(),
            transaction.getStatus(),
            transaction.getType(),
            transaction.getAmount(),
            transaction.getOppositePartyType(),
            transaction.getOppositeParty()));
    return transaction;
  }

  public Transaction withdraw(BigDecimal amount, String destination, OppositePartyType partyType) {
    if (!activeForWithdraw) {
      throw new IllegalStateException("Withdraw not allowed for this wallet");
    }

    if (usableBalance.compareTo(amount) < 0) {
      throw new IllegalStateException("Insufficient usable balance");
    }

    Status status =
        amount.compareTo(BigDecimal.valueOf(1000)) > 0 ? Status.PENDING : Status.APPROVED;

    Transaction transaction =
        Transaction.withdraw(this.getId(), amount, destination, partyType, status);

    usableBalance = usableBalance.subtract(amount);
    if (status == Status.APPROVED) {
      balance = balance.subtract(amount);
    }

    this.registerEvent(
        new TransactionCreatedEvent(
            transaction.getId(),
            transaction.getWalletId(),
            transaction.getStatus(),
            transaction.getType(),
            transaction.getAmount(),
            transaction.getOppositePartyType(),
            transaction.getOppositeParty()));
    return transaction;
  }

  public void applyTransactionApproval(Transaction transaction, Status newStatus) {
    if (transaction.getStatus() != Status.PENDING) {
      throw new IllegalStateException("Only PENDING transactions can be approved or denied");
    }

    transaction.setStatus(newStatus);

    if (transaction.getType() == Type.DEPOSIT && newStatus == Status.APPROVED) {
      usableBalance = usableBalance.add(transaction.getAmount());
    } else if (transaction.getType() == Type.WITHDRAW && newStatus == Status.APPROVED) {
      balance = balance.subtract(transaction.getAmount());
    } else if (newStatus == Status.DENIED) {
      // Refund usable balance if it was withdraw
      if (transaction.getType() == Type.WITHDRAW) {
        usableBalance = usableBalance.add(transaction.getAmount());
      }
    }

    this.registerEvent(new TransactionStatusChangedEvent(
        transaction.getId(),
        transaction.getWalletId(),
        newStatus,
        transaction.getType(),
        transaction.getAmount(),
        transaction.getOppositePartyType(),
        transaction.getOppositeParty()));
  }
}
