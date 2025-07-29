package ecetin.digiwallet.hub.wallet.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class WalletTest {

    @Test
    void testCreateWallet() {
        // Given
        UUID walletId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        String name = "Test Wallet";
        String currency = "USD";

        // When
        Wallet wallet = new Wallet(customerId, name, currency);

        // Then
        assertEquals(customerId, wallet.getCustomerId());
        assertEquals(name, wallet.getName());
        assertEquals(currency, wallet.getCurrency().getValue());
        assertEquals(BigDecimal.ZERO, wallet.getBalance());
        assertEquals(BigDecimal.ZERO, wallet.getUsableBalance());
        assertTrue(wallet.isActiveForShopping());
        assertTrue(wallet.isActiveForWithdraw());
    }

    @Test
    void testCreateWalletWithShoppingAndWithdrawSettings() {
        // Given
        UUID walletId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        String name = "Test Wallet";
        String currency = "EUR";
        boolean activeForShopping = false;
        boolean activeForWithdraw = false;

        // When
        Wallet wallet = new Wallet(customerId, name, currency, activeForShopping, activeForWithdraw);

        // Then
        assertEquals(customerId, wallet.getCustomerId());
        assertEquals(name, wallet.getName());
        assertEquals(currency, wallet.getCurrency().getValue());
        assertEquals(BigDecimal.ZERO, wallet.getBalance());
        assertEquals(BigDecimal.ZERO, wallet.getUsableBalance());
        assertFalse(wallet.isActiveForShopping());
        assertFalse(wallet.isActiveForWithdraw());
    }

    @ParameterizedTest
    @ValueSource(strings = {"TRY", "USD", "EUR"})
    void testCreateWalletWithValidCurrencies(String currency) {
        // Given
        UUID walletId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        String name = "Test Wallet";

        // When/Then
        Wallet wallet = new Wallet(customerId, name, currency);
        assertEquals(currency, wallet.getCurrency().getValue());
    }

    @Test
    void testCreateWalletWithInvalidCurrency() {
        // Given
        UUID walletId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        String name = "Test Wallet";
        String currency = "GBP"; // Invalid currency

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            new Wallet(customerId, name, currency);
        });
    }

    @Test
    void testDepositSmallAmount() {
        // Given
        Wallet wallet = new Wallet(UUID.randomUUID(), "Test Wallet", "USD");
        BigDecimal amount = new BigDecimal("500.00");
        String source = "123456789";
        OppositePartyType partyType = OppositePartyType.IBAN;

        // When
        Transaction transaction = wallet.deposit(amount, source, partyType);

        // Then
        assertEquals(Status.APPROVED, transaction.getStatus());
        assertEquals(Type.DEPOSIT, transaction.getType());
        assertEquals(amount, transaction.getAmount());
        assertEquals(source, transaction.getOppositeParty());
        assertEquals(partyType, transaction.getOppositePartyType());
        assertEquals(amount, wallet.getBalance());
        assertEquals(amount, wallet.getUsableBalance());
    }

    @Test
    void testDepositLargeAmount() {
        // Given
        Wallet wallet = new Wallet(UUID.randomUUID(), "Test Wallet", "USD");
        BigDecimal amount = new BigDecimal("1500.00");
        String source = "123456789";
        OppositePartyType partyType = OppositePartyType.IBAN;

        // When
        Transaction transaction = wallet.deposit(amount, source, partyType);

        // Then
        assertEquals(Status.PENDING, transaction.getStatus());
        assertEquals(Type.DEPOSIT, transaction.getType());
        assertEquals(amount, transaction.getAmount());
        assertEquals(source, transaction.getOppositeParty());
        assertEquals(partyType, transaction.getOppositePartyType());
        assertEquals(amount, wallet.getBalance());
        assertEquals(BigDecimal.ZERO, wallet.getUsableBalance());
    }

    @Test
    void testWithdrawSmallAmount() {
        // Given
        Wallet wallet = new Wallet(UUID.randomUUID(), "Test Wallet", "USD");
        BigDecimal depositAmount = new BigDecimal("1000.00");
        wallet.deposit(depositAmount, "123456789", OppositePartyType.IBAN);

        BigDecimal withdrawAmount = new BigDecimal("500.00");
        String destination = "987654321";
        OppositePartyType partyType = OppositePartyType.IBAN;

        // When
        Transaction transaction = wallet.withdraw(withdrawAmount, destination, partyType);

        // Then
        assertEquals(Status.APPROVED, transaction.getStatus());
        assertEquals(Type.WITHDRAW, transaction.getType());
        assertEquals(withdrawAmount, transaction.getAmount());
        assertEquals(destination, transaction.getOppositeParty());
        assertEquals(partyType, transaction.getOppositePartyType());
        assertEquals(depositAmount.subtract(withdrawAmount), wallet.getBalance());
        assertEquals(depositAmount.subtract(withdrawAmount), wallet.getUsableBalance());
    }

    @Test
    void testWithdrawLargeAmount() {
        // Given
        Wallet wallet = new Wallet(UUID.randomUUID(), "Test Wallet", "USD");
        BigDecimal depositAmount = new BigDecimal("2000.00");
        Transaction depositTransaction = wallet.deposit(depositAmount, "123456789", OppositePartyType.IBAN);
        // Approve the deposit to make funds available in usable balance
        wallet.applyTransactionApproval(depositTransaction, Status.APPROVED);

        BigDecimal withdrawAmount = new BigDecimal("1500.00");
        String destination = "987654321";
        OppositePartyType partyType = OppositePartyType.IBAN;

        // When
        Transaction transaction = wallet.withdraw(withdrawAmount, destination, partyType);

        // Then
        assertEquals(Status.PENDING, transaction.getStatus());
        assertEquals(Type.WITHDRAW, transaction.getType());
        assertEquals(withdrawAmount, transaction.getAmount());
        assertEquals(destination, transaction.getOppositeParty());
        assertEquals(partyType, transaction.getOppositePartyType());
        assertEquals(depositAmount, wallet.getBalance());
        assertEquals(depositAmount.subtract(withdrawAmount), wallet.getUsableBalance());
    }

    @Test
    void testWithdrawFromInactiveWallet() {
        // Given
        Wallet wallet = new Wallet(UUID.randomUUID(), "Test Wallet", "USD", true, false);
        BigDecimal depositAmount = new BigDecimal("1000.00");
        wallet.deposit(depositAmount, "123456789", OppositePartyType.IBAN);

        BigDecimal withdrawAmount = new BigDecimal("500.00");
        String destination = "987654321";
        OppositePartyType partyType = OppositePartyType.IBAN;

        // When/Then
        assertThrows(IllegalStateException.class, () -> {
            wallet.withdraw(withdrawAmount, destination, partyType);
        });
    }

    @Test
    void testWithdrawInsufficientBalance() {
        // Given
        Wallet wallet = new Wallet(UUID.randomUUID(), "Test Wallet", "USD");
        BigDecimal depositAmount = new BigDecimal("500.00");
        wallet.deposit(depositAmount, "123456789", OppositePartyType.IBAN);

        BigDecimal withdrawAmount = new BigDecimal("1000.00");
        String destination = "987654321";
        OppositePartyType partyType = OppositePartyType.IBAN;

        // When/Then
        assertThrows(IllegalStateException.class, () -> {
            wallet.withdraw(withdrawAmount, destination, partyType);
        });
    }

    @Test
    void testApproveDepositTransaction() {
        // Given
        Wallet wallet = new Wallet(UUID.randomUUID(), "Test Wallet", "USD");
        BigDecimal amount = new BigDecimal("1500.00");
        Transaction transaction = wallet.deposit(amount, "123456789", OppositePartyType.IBAN);

        // Initial state
        assertEquals(Status.PENDING, transaction.getStatus());
        assertEquals(amount, wallet.getBalance());
        assertEquals(BigDecimal.ZERO, wallet.getUsableBalance());

        // When
        wallet.applyTransactionApproval(transaction, Status.APPROVED);

        // Then
        assertEquals(Status.APPROVED, transaction.getStatus());
        assertEquals(amount, wallet.getBalance());
        assertEquals(amount, wallet.getUsableBalance());
    }

    @Test
    void testDenyDepositTransaction() {
        // Given
        Wallet wallet = new Wallet(UUID.randomUUID(), "Test Wallet", "USD");
        BigDecimal amount = new BigDecimal("1500.00");
        Transaction transaction = wallet.deposit(amount, "123456789", OppositePartyType.IBAN);

        // Initial state
        assertEquals(Status.PENDING, transaction.getStatus());
        assertEquals(amount, wallet.getBalance());
        assertEquals(BigDecimal.ZERO, wallet.getUsableBalance());

        // When
        wallet.applyTransactionApproval(transaction, Status.DENIED);

        // Then
        assertEquals(Status.DENIED, transaction.getStatus());
        assertEquals(amount, wallet.getBalance());
        assertEquals(BigDecimal.ZERO, wallet.getUsableBalance());
    }

    @Test
    void testApproveWithdrawTransaction() {
        // Given
        Wallet wallet = new Wallet(UUID.randomUUID(), "Test Wallet", "USD");
        BigDecimal depositAmount = new BigDecimal("2000.00");
        Transaction depositTransaction = wallet.deposit(depositAmount, "123456789", OppositePartyType.IBAN);
        // Approve the deposit to make funds available in usable balance
        wallet.applyTransactionApproval(depositTransaction, Status.APPROVED);

        BigDecimal withdrawAmount = new BigDecimal("1500.00");
        Transaction transaction = wallet.withdraw(withdrawAmount, "987654321", OppositePartyType.IBAN);

        // Initial state
        assertEquals(Status.PENDING, transaction.getStatus());
        assertEquals(depositAmount, wallet.getBalance());
        assertEquals(depositAmount.subtract(withdrawAmount), wallet.getUsableBalance());

        // When
        wallet.applyTransactionApproval(transaction, Status.APPROVED);

        // Then
        assertEquals(Status.APPROVED, transaction.getStatus());
        assertEquals(depositAmount.subtract(withdrawAmount), wallet.getBalance());
        assertEquals(depositAmount.subtract(withdrawAmount), wallet.getUsableBalance());
    }

    @Test
    void testDenyWithdrawTransaction() {
        // Given
        Wallet wallet = new Wallet(UUID.randomUUID(), "Test Wallet", "USD");
        BigDecimal depositAmount = new BigDecimal("2000.00");
        Transaction depositTransaction = wallet.deposit(depositAmount, "123456789", OppositePartyType.IBAN);
        // Approve the deposit to make funds available in usable balance
        wallet.applyTransactionApproval(depositTransaction, Status.APPROVED);

        BigDecimal withdrawAmount = new BigDecimal("1500.00");
        Transaction transaction = wallet.withdraw(withdrawAmount, "987654321", OppositePartyType.IBAN);

        // Initial state
        assertEquals(Status.PENDING, transaction.getStatus());
        assertEquals(depositAmount, wallet.getBalance());
        assertEquals(depositAmount.subtract(withdrawAmount), wallet.getUsableBalance());

        // When
        wallet.applyTransactionApproval(transaction, Status.DENIED);

        // Then
        assertEquals(Status.DENIED, transaction.getStatus());
        assertEquals(depositAmount, wallet.getBalance());
        assertEquals(depositAmount, wallet.getUsableBalance()); // Usable balance is refunded
    }

    @Test
    void testApproveNonPendingTransaction() {
        // Given
        Wallet wallet = new Wallet(UUID.randomUUID(), "Test Wallet", "USD");
        BigDecimal amount = new BigDecimal("500.00");
        Transaction transaction = wallet.deposit(amount, "123456789", OppositePartyType.IBAN);

        // Transaction is already APPROVED because amount < 1000
        assertEquals(Status.APPROVED, transaction.getStatus());

        // When/Then
        assertThrows(IllegalStateException.class, () -> {
            wallet.applyTransactionApproval(transaction, Status.APPROVED);
        });
    }
}
