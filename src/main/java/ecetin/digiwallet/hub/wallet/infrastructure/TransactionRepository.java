package ecetin.digiwallet.hub.wallet.infrastructure;

import ecetin.digiwallet.hub.wallet.domain.Status;
import ecetin.digiwallet.hub.wallet.domain.Transaction;
import ecetin.digiwallet.hub.wallet.domain.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByWalletId(UUID walletId);
    List<Transaction> findByWalletIdAndStatus(UUID walletId, Status status);
    List<Transaction> findByWalletIdAndType(UUID walletId, Type type);
}