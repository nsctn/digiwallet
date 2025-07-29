package ecetin.digiwallet.hub.wallet.interfaces.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletResponse(
    UUID id,
    UUID customerId,
    String name,
    String currency,
    boolean activeForShopping,
    boolean activeForWithdraw,
    BigDecimal balance,
    BigDecimal usableBalance
) {
    public static WalletResponse fromWallet(ecetin.digiwallet.hub.wallet.domain.Wallet wallet) {
        return new WalletResponse(
            wallet.getId(),
            wallet.getCustomerId(),
            wallet.getName(),
            wallet.getCurrency().getValue(),
            wallet.isActiveForShopping(),
            wallet.isActiveForWithdraw(),
            wallet.getBalance(),
            wallet.getUsableBalance()
        );
    }
}