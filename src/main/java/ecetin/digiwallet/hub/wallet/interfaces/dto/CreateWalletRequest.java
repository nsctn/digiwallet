package ecetin.digiwallet.hub.wallet.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateWalletRequest(
    @NotNull(message = "Customer ID is required") UUID customerId,
    @NotBlank(message = "Wallet name is required") String walletName,
    @NotBlank(message = "Currency is required") String currency,
    boolean activeForShopping,
    boolean activeForWithdraw) {

  public CreateWalletRequest(UUID customerId, String walletName, String currency) {
    this(customerId, walletName, currency, true, true);
  }
}
