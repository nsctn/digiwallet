package ecetin.digiwallet.hub.wallet.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TransactionTest {

    @Test
    void testCreateDepositTransaction() {
        // Given
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("500.00");
        String source = "123456789";
        OppositePartyType partyType = OppositePartyType.IBAN;
        Status status = Status.APPROVED;
        
        // When
        Transaction transaction = Transaction.deposit(walletId, amount, source, partyType, status);
        
        // Then
        assertEquals(walletId, transaction.getWalletId());
        assertEquals(amount, transaction.getAmount());
        assertEquals(Type.DEPOSIT, transaction.getType());
        assertEquals(status, transaction.getStatus());
        assertEquals(partyType, transaction.getOppositePartyType());
        assertEquals(source, transaction.getOppositeParty());
    }
    
    @Test
    void testCreateWithdrawTransaction() {
        // Given
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("500.00");
        String destination = "987654321";
        OppositePartyType partyType = OppositePartyType.PAYMENT;
        Status status = Status.APPROVED;
        
        // When
        Transaction transaction = Transaction.withdraw(walletId, amount, destination, partyType, status);
        
        // Then
        assertEquals(walletId, transaction.getWalletId());
        assertEquals(amount, transaction.getAmount());
        assertEquals(Type.WITHDRAW, transaction.getType());
        assertEquals(status, transaction.getStatus());
        assertEquals(partyType, transaction.getOppositePartyType());
        assertEquals(destination, transaction.getOppositeParty());
    }
    
    @Test
    void testUpdateStatusFromPendingToApproved() {
        // Given
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("1500.00");
        String source = "123456789";
        OppositePartyType partyType = OppositePartyType.IBAN;
        Status initialStatus = Status.PENDING;
        
        Transaction transaction = Transaction.deposit(walletId, amount, source, partyType, initialStatus);
        assertEquals(Status.PENDING, transaction.getStatus());
        
        // When
        transaction.setStatus(Status.APPROVED);
        
        // Then
        assertEquals(Status.APPROVED, transaction.getStatus());
    }
    
    @Test
    void testUpdateStatusFromPendingToDenied() {
        // Given
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("1500.00");
        String source = "123456789";
        OppositePartyType partyType = OppositePartyType.IBAN;
        Status initialStatus = Status.PENDING;
        
        Transaction transaction = Transaction.deposit(walletId, amount, source, partyType, initialStatus);
        assertEquals(Status.PENDING, transaction.getStatus());
        
        // When
        transaction.setStatus(Status.DENIED);
        
        // Then
        assertEquals(Status.DENIED, transaction.getStatus());
    }
    
    @Test
    void testUpdateStatusFromApprovedThrowsException() {
        // Given
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("500.00");
        String source = "123456789";
        OppositePartyType partyType = OppositePartyType.IBAN;
        Status initialStatus = Status.APPROVED;
        
        Transaction transaction = Transaction.deposit(walletId, amount, source, partyType, initialStatus);
        assertEquals(Status.APPROVED, transaction.getStatus());
        
        // When/Then
        assertThrows(IllegalStateException.class, () -> {
            transaction.setStatus(Status.DENIED);
        });
    }
    
    @Test
    void testUpdateStatusFromDeniedThrowsException() {
        // Given
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("500.00");
        String source = "123456789";
        OppositePartyType partyType = OppositePartyType.IBAN;
        Status initialStatus = Status.DENIED;
        
        Transaction transaction = Transaction.deposit(walletId, amount, source, partyType, initialStatus);
        assertEquals(Status.DENIED, transaction.getStatus());
        
        // When/Then
        assertThrows(IllegalStateException.class, () -> {
            transaction.setStatus(Status.APPROVED);
        });
    }
    
    @Test
    void testDepositTransactionWithLargeAmount() {
        // Given
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("1500.00");
        String source = "123456789";
        OppositePartyType partyType = OppositePartyType.IBAN;
        
        // When
        Transaction transaction = Transaction.deposit(walletId, amount, source, partyType, Status.PENDING);
        
        // Then
        assertEquals(Status.PENDING, transaction.getStatus());
        assertEquals(Type.DEPOSIT, transaction.getType());
    }
    
    @Test
    void testWithdrawTransactionWithLargeAmount() {
        // Given
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("1500.00");
        String destination = "987654321";
        OppositePartyType partyType = OppositePartyType.PAYMENT;
        
        // When
        Transaction transaction = Transaction.withdraw(walletId, amount, destination, partyType, Status.PENDING);
        
        // Then
        assertEquals(Status.PENDING, transaction.getStatus());
        assertEquals(Type.WITHDRAW, transaction.getType());
    }
}