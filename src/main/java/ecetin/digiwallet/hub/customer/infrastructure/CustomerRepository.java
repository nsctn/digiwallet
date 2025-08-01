package ecetin.digiwallet.hub.customer.infrastructure;

import ecetin.digiwallet.hub.customer.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByTckn(String tckn);
}