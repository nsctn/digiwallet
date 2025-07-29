package ecetin.digiwallet.hub.wallet.application;

import ecetin.digiwallet.hub.wallet.domain.Status;
import ecetin.digiwallet.hub.wallet.domain.Transaction;
import ecetin.digiwallet.hub.wallet.domain.Type;
import ecetin.digiwallet.hub.wallet.domain.Wallet;
import ecetin.digiwallet.hub.wallet.infrastructure.TransactionRepository;
import ecetin.digiwallet.hub.wallet.infrastructure.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletService walletService;
    private final WalletRepository walletRepository;

    /**
     * Finds a transaction by its ID.
     *
     * @param authenticatedCustomerId The ID of the authenticated customer
     * @param id The ID of the transaction
     * @param isEmployee Whether the authenticated user is an employee
     * @return The transaction
     */
    @Transactional(readOnly = true)
    public Transaction findById(UUID authenticatedCustomerId, UUID id, boolean isEmployee) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        checkWalletAccess(authenticatedCustomerId, transaction.getWalletId(), isEmployee);
        return transaction;
    }

    /**
     * Finds all transactions for a wallet.
     *
     * @param authenticatedCustomerId The ID of the authenticated customer
     * @param walletId The ID of the wallet
     * @param isEmployee Whether the authenticated user is an employee
     * @return A list of transactions
     */
    @Transactional(readOnly = true)
    public List<Transaction> findByWalletId(UUID authenticatedCustomerId, UUID walletId, boolean isEmployee) {
        checkWalletAccess(authenticatedCustomerId, walletId, isEmployee);
        return transactionRepository.findByWalletId(walletId);
    }

    /**
     * Finds all transactions for a wallet with a specific status.
     *
     * @param authenticatedCustomerId The ID of the authenticated customer
     * @param walletId The ID of the wallet
     * @param status The status to filter by
     * @param isEmployee Whether the authenticated user is an employee
     * @return A list of transactions
     */
    @Transactional(readOnly = true)
    public List<Transaction> findByWalletIdAndStatus(UUID authenticatedCustomerId, UUID walletId, Status status, boolean isEmployee) {
        checkWalletAccess(authenticatedCustomerId, walletId, isEmployee);
        return transactionRepository.findByWalletIdAndStatus(walletId, status);
    }

    /**
     * Finds all transactions for a wallet with a specific type.
     *
     * @param authenticatedCustomerId The ID of the authenticated customer
     * @param walletId The ID of the wallet
     * @param type The type to filter by
     * @param isEmployee Whether the authenticated user is an employee
     * @return A list of transactions
     */
    @Transactional(readOnly = true)
    public List<Transaction> findByWalletIdAndType(UUID authenticatedCustomerId, UUID walletId, Type type, boolean isEmployee) {
        checkWalletAccess(authenticatedCustomerId, walletId, isEmployee);
        return transactionRepository.findByWalletIdAndType(walletId, type);
    }

    /**
     * Approves or denies a transaction.
     *
     * @param authenticatedCustomerId The ID of the authenticated customer
     * @param transactionId The ID of the transaction
     * @param newStatus The new status of the transaction
     * @param isEmployee Whether the authenticated user is an employee
     * @return The updated transaction
     */
    @Transactional
    public Transaction approveTransaction(UUID authenticatedCustomerId, UUID transactionId, Status newStatus, boolean isEmployee) {
        return walletService.approveTransaction(authenticatedCustomerId, transactionId, newStatus, isEmployee);
    }
    
    /**
     * Checks if the authenticated user has access to the wallet.
     * Throws AccessDeniedException if the user doesn't have access.
     *
     * @param authenticatedCustomerId The ID of the authenticated customer
     * @param walletId The wallet ID to check access for
     * @param isEmployee Whether the authenticated user is an employee
     */
    private void checkWalletAccess(UUID authenticatedCustomerId, UUID walletId, boolean isEmployee) {
        // If user is an employee, they can access any wallet
        if (isEmployee) {
            return;
        }
        
        // Otherwise, check if the user is accessing their own wallet
        if (authenticatedCustomerId == null) {
            throw new AccessDeniedException("Authentication required");
        }
        
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
                
        if (!authenticatedCustomerId.equals(wallet.getCustomerId())) {
            throw new AccessDeniedException("You can only access your own wallets");
        }
    }
}