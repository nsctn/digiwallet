package ecetin.digiwallet.hub.wallet.application;

import ecetin.digiwallet.hub.wallet.domain.*;
import ecetin.digiwallet.hub.wallet.infrastructure.TransactionRepository;
import ecetin.digiwallet.hub.wallet.infrastructure.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Creates a new wallet for the specified customer.
     * If the authenticated customer ID doesn't match the target customer ID,
     * the method will check if the authenticated user is an employee.
     *
     * @param authenticatedCustomerId The ID of the authenticated customer
     * @param targetCustomerId The ID of the customer for whom the wallet is being created
     * @param name The name of the wallet
     * @param currency The currency of the wallet
     * @param activeForShopping Whether the wallet is active for shopping
     * @param activeForWithdraw Whether the wallet is active for withdrawals
     * @param isEmployee Whether the authenticated user is an employee
     * @return The created wallet
     */
    @Transactional
    public Wallet createWallet(UUID authenticatedCustomerId, UUID targetCustomerId, String name, 
                              String currency, boolean activeForShopping, boolean activeForWithdraw, 
                              boolean isEmployee) {
        // Check if the authenticated user is creating a wallet for themselves or is an employee
        checkCustomerAccess(authenticatedCustomerId, targetCustomerId, isEmployee);
        
        Wallet wallet = new Wallet(targetCustomerId, name, currency, activeForShopping, activeForWithdraw);
        return walletRepository.save(wallet);
    }

    /**
     * Lists all wallets for the specified customer.
     *
     * @param authenticatedCustomerId The ID of the authenticated customer
     * @param targetCustomerId The ID of the customer whose wallets are being listed
     * @param isEmployee Whether the authenticated user is an employee
     * @return A list of wallets
     */
    @Transactional(readOnly = true)
    public List<Wallet> listWallets(UUID authenticatedCustomerId, UUID targetCustomerId, boolean isEmployee) {
        checkCustomerAccess(authenticatedCustomerId, targetCustomerId, isEmployee);
        return walletRepository.findByCustomerId(targetCustomerId);
    }

    /**
     * Lists all wallets for the specified customer with the specified currency.
     *
     * @param authenticatedCustomerId The ID of the authenticated customer
     * @param targetCustomerId The ID of the customer whose wallets are being listed
     * @param currency The currency to filter by
     * @param isEmployee Whether the authenticated user is an employee
     * @return A list of wallets
     */
    @Transactional(readOnly = true)
    public List<Wallet> listWallets(UUID authenticatedCustomerId, UUID targetCustomerId, String currency, boolean isEmployee) {
        checkCustomerAccess(authenticatedCustomerId, targetCustomerId, isEmployee);
        return walletRepository.findByCustomerIdAndCurrencyValue(targetCustomerId, currency);
    }

    /**
     * Deposits money into the specified wallet.
     *
     * @param authenticatedCustomerId The ID of the authenticated customer
     * @param walletId The ID of the wallet
     * @param amount The amount to deposit
     * @param source The source of the deposit
     * @param partyType The type of the opposite party
     * @param isEmployee Whether the authenticated user is an employee
     * @return The created transaction
     */
    @Transactional
    public Transaction deposit(UUID authenticatedCustomerId, UUID walletId, BigDecimal amount, 
                              String source, OppositePartyType partyType, boolean isEmployee) {
        Wallet wallet = getWalletWithAccessCheck(authenticatedCustomerId, walletId, isEmployee);
        Transaction transaction = wallet.deposit(amount, source, partyType);
        walletRepository.save(wallet);
        return transactionRepository.save(transaction);
    }

    /**
     * Withdraws money from the specified wallet.
     *
     * @param authenticatedCustomerId The ID of the authenticated customer
     * @param walletId The ID of the wallet
     * @param amount The amount to withdraw
     * @param destination The destination of the withdrawal
     * @param partyType The type of the opposite party
     * @param isEmployee Whether the authenticated user is an employee
     * @return The created transaction
     */
    @Transactional
    public Transaction withdraw(UUID authenticatedCustomerId, UUID walletId, BigDecimal amount, 
                               String destination, OppositePartyType partyType, boolean isEmployee) {
        Wallet wallet = getWalletWithAccessCheck(authenticatedCustomerId, walletId, isEmployee);
        Transaction transaction = wallet.withdraw(amount, destination, partyType);
        walletRepository.save(wallet);
        return transactionRepository.save(transaction);
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
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        
        Wallet wallet = getWalletWithAccessCheck(authenticatedCustomerId, transaction.getWalletId(), isEmployee);
        wallet.applyTransactionApproval(transaction, newStatus);
        
        walletRepository.save(wallet);
        return transactionRepository.save(transaction);
    }

    /**
     * Gets a wallet and checks if the authenticated user has access to it.
     *
     * @param authenticatedCustomerId The ID of the authenticated customer
     * @param walletId The ID of the wallet
     * @param isEmployee Whether the authenticated user is an employee
     * @return The wallet
     */
    private Wallet getWalletWithAccessCheck(UUID authenticatedCustomerId, UUID walletId, boolean isEmployee) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
        
        checkCustomerAccess(authenticatedCustomerId, wallet.getCustomerId(), isEmployee);
        return wallet;
    }

    /**
     * Checks if the authenticated user has access to the specified customer's data.
     *
     * @param authenticatedCustomerId The ID of the authenticated customer
     * @param targetCustomerId The ID of the target customer
     * @param isEmployee Whether the authenticated user is an employee
     */
    private void checkCustomerAccess(UUID authenticatedCustomerId, UUID targetCustomerId, boolean isEmployee) {
        // If a user is an employee, they can access any customer's data
        if (isEmployee) {
            return;
        }
        
        // Otherwise, check if the user is accessing their own data
        if (authenticatedCustomerId == null || !authenticatedCustomerId.equals(targetCustomerId)) {
            throw new AccessDeniedException("You can only access your own data");
        }
    }
}