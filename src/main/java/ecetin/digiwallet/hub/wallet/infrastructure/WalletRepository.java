package ecetin.digiwallet.hub.wallet.infrastructure;

import ecetin.digiwallet.hub.wallet.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    List<Wallet> findByCustomerId(UUID customerId);
    List<Wallet> findByCustomerIdAndCurrencyValue(UUID customerId, String currencyValue);
}