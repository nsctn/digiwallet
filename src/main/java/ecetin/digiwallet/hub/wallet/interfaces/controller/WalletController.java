package ecetin.digiwallet.hub.wallet.interfaces.controller;

import ecetin.digiwallet.hub.common.security.CustomerId;
import ecetin.digiwallet.hub.common.security.IsEmployee;
import ecetin.digiwallet.hub.wallet.application.TransactionService;
import ecetin.digiwallet.hub.wallet.application.WalletService;
import ecetin.digiwallet.hub.wallet.domain.Transaction;
import ecetin.digiwallet.hub.wallet.domain.Wallet;
import ecetin.digiwallet.hub.wallet.interfaces.dto.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

  private final WalletService walletService;
  private final TransactionService transactionService;

  @PostMapping
  @PreAuthorize("hasRole('WALLET:MANAGE')")
  public ResponseEntity<WalletResponse> createWallet(
      @CustomerId UUID authenticatedCustomerId,
      @IsEmployee boolean isEmployee,
      @Valid @RequestBody CreateWalletRequest request) {
    
    Wallet wallet =
        walletService.createWallet(
            authenticatedCustomerId,
            request.customerId(),
            request.walletName(),
            request.currency(),
            request.activeForShopping(),
            request.activeForWithdraw(),
            isEmployee);
    return ResponseEntity.status(HttpStatus.CREATED).body(WalletResponse.fromWallet(wallet));
  }

  @GetMapping
  @PreAuthorize("hasRole('WALLET:VIEW')")
  public ResponseEntity<List<WalletResponse>> listWallets(
      @CustomerId UUID authenticatedCustomerId,
      @IsEmployee boolean isEmployee,
      @RequestParam UUID customerId, 
      @RequestParam(required = false) String currency) {
    
    List<Wallet> wallets;
    
    if (currency != null && !currency.isEmpty()) {
      wallets = walletService.listWallets(authenticatedCustomerId, customerId, currency, isEmployee);
    } else {
      wallets = walletService.listWallets(authenticatedCustomerId, customerId, isEmployee);
    }

    List<WalletResponse> response =
        wallets.stream().map(WalletResponse::fromWallet).collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }

  @PostMapping("/deposit")
  @PreAuthorize("hasRole('WALLET:DEPOSIT')")
  public ResponseEntity<TransactionResponse> deposit(
      @CustomerId UUID authenticatedCustomerId,
      @IsEmployee boolean isEmployee,
      @Valid @RequestBody DepositRequest request) {
    
    Transaction transaction =
        walletService.deposit(
            authenticatedCustomerId,
            request.walletId(),
            request.amount(),
            request.source(),
            request.sourceType(),
            isEmployee);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(TransactionResponse.fromTransaction(transaction));
  }

  @PostMapping("/withdraw")
  @PreAuthorize("hasRole('WALLET:WITHDRAW')")
  public ResponseEntity<TransactionResponse> withdraw(
      @CustomerId UUID authenticatedCustomerId,
      @IsEmployee boolean isEmployee,
      @Valid @RequestBody WithdrawRequest request) {
    
    Transaction transaction =
        walletService.withdraw(
            authenticatedCustomerId,
            request.walletId(),
            request.amount(),
            request.destination(),
            request.destinationType(),
            isEmployee);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(TransactionResponse.fromTransaction(transaction));
  }

  @GetMapping("/{walletId}/transactions")
  @PreAuthorize("hasRole('WALLET:VIEW')")
  public ResponseEntity<List<TransactionResponse>> listTransactions(
      @CustomerId UUID authenticatedCustomerId,
      @IsEmployee boolean isEmployee,
      @PathVariable UUID walletId) {
    
    List<Transaction> transactions = transactionService.findByWalletId(authenticatedCustomerId, walletId, isEmployee);
    List<TransactionResponse> response =
        transactions.stream()
            .map(TransactionResponse::fromTransaction)
            .collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }

  @PostMapping("/transactions/approve")
  @PreAuthorize("hasRole('TRANSACTION:APPROVE')")
  public ResponseEntity<TransactionResponse> approveTransaction(
      @CustomerId UUID authenticatedCustomerId,
      @IsEmployee boolean isEmployee,
      @Valid @RequestBody ApproveTransactionRequest request) {
    
    Transaction transaction =
        transactionService.approveTransaction(
            authenticatedCustomerId, 
            request.transactionId(), 
            request.status(),
            isEmployee);
    return ResponseEntity.ok(TransactionResponse.fromTransaction(transaction));
  }
}