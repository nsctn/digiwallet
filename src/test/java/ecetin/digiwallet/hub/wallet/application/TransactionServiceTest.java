package ecetin.digiwallet.hub.wallet.application;

import ecetin.digiwallet.hub.wallet.domain.Status;
import ecetin.digiwallet.hub.wallet.domain.Transaction;
import ecetin.digiwallet.hub.wallet.domain.Type;
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
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private TransactionService transactionService;

    private UUID customerId;
    private UUID walletId;
    private UUID transactionId;
    private Wallet wallet;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        walletId = UUID.randomUUID();
        transactionId = UUID.randomUUID();
        wallet = new Wallet(walletId, customerId, "Test Wallet", "USD");

        // Create a mock transaction
        transaction = mock(Transaction.class);
        // Use lenient() to avoid UnnecessaryStubbingException
        lenient().when(transaction.getWalletId()).thenReturn(walletId);
    }

    @Test
    void findById_WhenUserIsEmployee_ShouldReturnTransaction() {
        // Arrange
        UUID authenticatedCustomerId = UUID.randomUUID(); // Different from wallet owner

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        // Use lenient() since this stubbing is not used in this test
        lenient().when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        // Act
        Transaction result = transactionService.findById(authenticatedCustomerId, transactionId, true);

        // Assert
        assertNotNull(result);
        assertEquals(transaction, result);
        verify(transactionRepository).findById(transactionId);
        // No need to verify wallet access for employees
        verify(walletRepository, never()).findById(any());
    }

    @Test
    void findById_WhenUserIsCustomerAccessingOwnTransaction_ShouldReturnTransaction() {
        // Arrange
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        // Act
        Transaction result = transactionService.findById(customerId, transactionId, false);

        // Assert
        assertNotNull(result);
        assertEquals(transaction, result);
        verify(transactionRepository).findById(transactionId);
        verify(walletRepository).findById(walletId);
    }

    @Test
    void findById_WhenUserIsCustomerAccessingOtherCustomerTransaction_ShouldThrowAccessDeniedException() {
        // Arrange
        UUID authenticatedCustomerId = UUID.randomUUID(); // Different from wallet owner
        UUID otherCustomerId = UUID.randomUUID();
        Wallet otherWallet = new Wallet(walletId, otherCustomerId, "Other Wallet", "USD");

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(otherWallet));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            transactionService.findById(authenticatedCustomerId, transactionId, false);
        });
        verify(transactionRepository).findById(transactionId);
        verify(walletRepository).findById(walletId);
    }

    @Test
    void findByWalletId_WhenUserIsEmployee_ShouldReturnTransactions() {
        // Arrange
        UUID authenticatedCustomerId = UUID.randomUUID(); // Different from wallet owner
        List<Transaction> expectedTransactions = Arrays.asList(transaction);

        // Use lenient() since this stubbing is not used in this test
        lenient().when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByWalletId(walletId)).thenReturn(expectedTransactions);

        // Act
        List<Transaction> result = transactionService.findByWalletId(authenticatedCustomerId, walletId, true);

        // Assert
        assertEquals(expectedTransactions, result);
        // No need to verify wallet access for employees
        verify(walletRepository, never()).findById(any());
        verify(transactionRepository).findByWalletId(walletId);
    }

    @Test
    void findByWalletId_WhenUserIsCustomerAccessingOwnWallet_ShouldReturnTransactions() {
        // Arrange
        List<Transaction> expectedTransactions = Arrays.asList(transaction);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByWalletId(walletId)).thenReturn(expectedTransactions);

        // Act
        List<Transaction> result = transactionService.findByWalletId(customerId, walletId, false);

        // Assert
        assertEquals(expectedTransactions, result);
        verify(walletRepository).findById(walletId);
        verify(transactionRepository).findByWalletId(walletId);
    }

    @Test
    void findByWalletId_WhenUserIsCustomerAccessingOtherCustomerWallet_ShouldThrowAccessDeniedException() {
        // Arrange
        UUID authenticatedCustomerId = UUID.randomUUID(); // Different from wallet owner
        UUID otherCustomerId = UUID.randomUUID();
        Wallet otherWallet = new Wallet(walletId, otherCustomerId, "Other Wallet", "USD");

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(otherWallet));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            transactionService.findByWalletId(authenticatedCustomerId, walletId, false);
        });
        verify(walletRepository).findById(walletId);
        verify(transactionRepository, never()).findByWalletId(any());
    }

    @Test
    void findByWalletIdAndStatus_ShouldReturnTransactions() {
        // Arrange
        List<Transaction> expectedTransactions = Arrays.asList(transaction);
        Status status = Status.APPROVED;

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByWalletIdAndStatus(walletId, status)).thenReturn(expectedTransactions);

        // Act
        List<Transaction> result = transactionService.findByWalletIdAndStatus(customerId, walletId, status, false);

        // Assert
        assertEquals(expectedTransactions, result);
        verify(walletRepository).findById(walletId);
        verify(transactionRepository).findByWalletIdAndStatus(walletId, status);
    }

    @Test
    void findByWalletIdAndType_ShouldReturnTransactions() {
        // Arrange
        List<Transaction> expectedTransactions = Arrays.asList(transaction);
        Type type = Type.DEPOSIT;

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByWalletIdAndType(walletId, type)).thenReturn(expectedTransactions);

        // Act
        List<Transaction> result = transactionService.findByWalletIdAndType(customerId, walletId, type, false);

        // Assert
        assertEquals(expectedTransactions, result);
        verify(walletRepository).findById(walletId);
        verify(transactionRepository).findByWalletIdAndType(walletId, type);
    }

    @Test
    void approveTransaction_ShouldDelegateToWalletService() {
        // Arrange
        UUID authenticatedCustomerId = UUID.randomUUID();
        Status newStatus = Status.APPROVED;

        when(walletService.approveTransaction(authenticatedCustomerId, transactionId, newStatus, true))
            .thenReturn(transaction);

        // Act
        Transaction result = transactionService.approveTransaction(authenticatedCustomerId, transactionId, newStatus, true);

        // Assert
        assertEquals(transaction, result);
        verify(walletService).approveTransaction(authenticatedCustomerId, transactionId, newStatus, true);
    }
}
