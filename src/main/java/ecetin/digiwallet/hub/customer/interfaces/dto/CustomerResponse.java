package ecetin.digiwallet.hub.customer.interfaces.dto;

import ecetin.digiwallet.hub.customer.domain.Customer;

import java.util.UUID;

public record CustomerResponse(
    UUID id,
    String name,
    String surname,
    String tckn
) {
    public static CustomerResponse fromCustomer(Customer customer) {
        return new CustomerResponse(
            customer.getId(),
            customer.getName(),
            customer.getSurname(),
            customer.getTckn()
        );
    }
}