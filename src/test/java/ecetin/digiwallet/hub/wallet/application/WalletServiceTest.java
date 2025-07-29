package ecetin.digiwallet.hub.wallet.application;

import ecetin.digiwallet.hub.wallet.domain.OppositePartyType;
import ecetin.digiwallet.hub.wallet.domain.Status;
import ecetin.digiwallet.hub.wallet.domain.Transaction;
import ecetin.digiwallet.hub.wallet.domain.Wallet;
import ecetin.digiwallet.hub.wallet.infrastructure.TransactionRepository;
import ecetin.digiwallet.hub.wallet.infrastructure.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private WalletService walletService;

    private UUID customerId;
    private UUID walletId;
    private Wallet wallet;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        walletId = UUID.randomUUID();
        wallet = new Wallet(walletId, customerId, "Test Wallet", "USD");
        transaction = wallet.deposit(new BigDecimal("100.00"), "123456789", OppositePartyType.IBAN);
    }

    @Test
    void createWallet_WhenUserIsEmployee_ShouldCreateWallet() {
        // Arrange
        UUID authenticatedCustomerId = UUID.randomUUID(); // Different from target customer
        UUID targetCustomerId = customerId;
        String name = "New Wallet";
        String currency = "USD";
        boolean activeForShopping = true;
        boolean activeForWithdraw = true;

        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        // Act
        Wallet result = walletService.createWallet(authenticatedCustomerId, targetCustomerId, name, currency, 
                                                 activeForShopping, activeForWithdraw, true);

        // Assert
        assertNotNull(result);
        assertEquals(wallet, result);
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void createWallet_WhenUserIsCustomerCreatingOwnWallet_ShouldCreateWallet() {
        // Arrange
        String name = "New Wallet";
        String currency = "USD";
        boolean activeForShopping = true;
        boolean activeForWithdraw = true;

        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        // Act
        Wallet result = walletService.createWallet(customerId, customerId, name, currency, 
                                                 activeForShopping, activeForWithdraw, false);

        // Assert
        assertNotNull(result);
        assertEquals(wallet, result);
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void createWallet_WhenUserIsCustomerCreatingOtherCustomerWallet_ShouldThrowAccessDeniedException() {
        // Arrange
        UUID authenticatedCustomerId = UUID.randomUUID(); // Different from target customer
        UUID targetCustomerId = customerId;
        String name = "New Wallet";
        String currency = "USD";
        boolean activeForShopping = true;
        boolean activeForWithdraw = true;

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            walletService.createWallet(authenticatedCustomerId, targetCustomerId, name, currency, 
                                     activeForShopping, activeForWithdraw, false);
        });
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void listWallets_WhenUserIsEmployee_ShouldReturnWallets() {
        // Arrange
        UUID authenticatedCustomerId = UUID.randomUUID(); // Different from target customer
        List<Wallet> expectedWallets = Arrays.asList(wallet);
        when(walletRepository.findByCustomerId(customerId)).thenReturn(expectedWallets);

        // Act
        List<Wallet> result = walletService.listWallets(authenticatedCustomerId, customerId, true);

        // Assert
        assertEquals(expectedWallets, result);
        verify(walletRepository).findByCustomerId(customerId);
    }

    @Test
    void listWallets_WhenUserIsCustomerAccessingOwnWallets_ShouldReturnWallets() {
        // Arrange
        List<Wallet> expectedWallets = Arrays.asList(wallet);
        when(walletRepository.findByCustomerId(customerId)).thenReturn(expectedWallets);

        // Act
        List<Wallet> result = walletService.listWallets(customerId, customerId, false);

        // Assert
        assertEquals(expectedWallets, result);
        verify(walletRepository).findByCustomerId(customerId);
    }

    @Test
    void listWallets_WhenUserIsCustomerAccessingOtherCustomerWallets_ShouldThrowAccessDeniedException() {
        // Arrange
        UUID authenticatedCustomerId = UUID.randomUUID(); // Different from target customer

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            walletService.listWallets(authenticatedCustomerId, customerId, false);
        });
        verify(walletRepository, never()).findByCustomerId(any());
    }

    @Test
    void deposit_WhenUserIsEmployee_ShouldDepositAndReturnTransaction() {
        // Arrange
        UUID authenticatedCustomerId = UUID.randomUUID(); // Different from wallet owner
        BigDecimal amount = new BigDecimal("200.00");
        String source = "987654321";
        OppositePartyType partyType = OppositePartyType.IBAN;

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        Transaction result = walletService.deposit(authenticatedCustomerId, walletId, amount, source, partyType, true);

        // Assert
        assertNotNull(result);
        assertEquals(transaction, result);
        verify(walletRepository).findById(walletId);
        verify(walletRepository).save(any(Wallet.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void deposit_WhenUserIsCustomerDepositingToOwnWallet_ShouldDepositAndReturnTransaction() {
        // Arrange
        BigDecimal amount = new BigDecimal("200.00");
        String source = "987654321";
        OppositePartyType partyType = OppositePartyType.IBAN;

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        Transaction result = walletService.deposit(customerId, walletId, amount, source, partyType, false);

        // Assert
        assertNotNull(result);
        assertEquals(transaction, result);
        verify(walletRepository).findById(walletId);
        verify(walletRepository).save(any(Wallet.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void deposit_WhenUserIsCustomerDepositingToOtherCustomerWallet_ShouldThrowAccessDeniedException() {
        // Arrange
        UUID authenticatedCustomerId = UUID.randomUUID(); // Different from wallet owner
        BigDecimal amount = new BigDecimal("200.00");
        String source = "987654321";
        OppositePartyType partyType = OppositePartyType.IBAN;

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            walletService.deposit(authenticatedCustomerId, walletId, amount, source, partyType, false);
        });
        verify(walletRepository).findById(walletId);
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void withdraw_WhenUserIsEmployee_ShouldWithdrawAndReturnTransaction() {
        // Arrange
        UUID authenticatedCustomerId = UUID.randomUUID(); // Different from wallet owner
        BigDecimal amount = new BigDecimal("50.00");
        String destination = "987654321";
        OppositePartyType partyType = OppositePartyType.IBAN;

        // Make sure wallet has enough balance
        wallet.deposit(new BigDecimal("100.00"), "123456789", OppositePartyType.IBAN);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        Transaction result = walletService.withdraw(authenticatedCustomerId, walletId, amount, destination, partyType, true);

        // Assert
        assertNotNull(result);
        assertEquals(transaction, result);
        verify(walletRepository).findById(walletId);
        verify(walletRepository).save(any(Wallet.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void approveTransaction_WhenUserIsEmployee_ShouldApproveAndReturnTransaction() {
        // Arrange
        UUID authenticatedCustomerId = UUID.randomUUID(); // Different from wallet owner
        UUID transactionId = UUID.randomUUID();

        // Create a mock transaction that will be returned by the repository
        Transaction mockTransaction = mock(Transaction.class);
        when(mockTransaction.getWalletId()).thenReturn(walletId);
        when(mockTransaction.getStatus()).thenReturn(Status.PENDING);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(mockTransaction));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

        // Act
        Transaction result = walletService.approveTransaction(authenticatedCustomerId, transactionId, Status.APPROVED, true);

        // Assert
        assertNotNull(result);
        assertEquals(mockTransaction, result);
        verify(transactionRepository).findById(transactionId);
        verify(walletRepository).findById(walletId);
        verify(walletRepository).save(any(Wallet.class));
        verify(transactionRepository).save(any(Transaction.class));
    }
}
