package ecetin.digiwallet.hub.customer.domain;

import ecetin.digiwallet.hub.common.model.BaseAggregateRoot;
import ecetin.digiwallet.hub.customer.domain.event.CustomerCreatedEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import java.util.UUID;

@Getter
@Entity
@Table(name = "CUSTOMER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer extends BaseAggregateRoot<UUID> {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @NaturalId
    @Column(nullable = false, unique = true)
    private String tckn;

    public Customer(String name, String surname, String tckn) {
        this.name = name;
        this.surname = surname;
        this.tckn = tckn;
    }
    
    /**
     * Registers a customer created event with the current customer's data.
     * This should be called after the customer is saved to ensure the ID is available.
     */
    public void registerCustomerCreatedEvent() {
        this.registerEvent(new CustomerCreatedEvent(this.getId(), this.name, this.surname, this.tckn));
    }
}