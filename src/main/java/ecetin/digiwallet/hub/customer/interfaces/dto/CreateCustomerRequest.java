package ecetin.digiwallet.hub.customer.interfaces.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCustomerRequest(
    @NotBlank(message = "Name is required") String name,
    @NotBlank(message = "Surname is required") String surname,
    @NotBlank(message = "TCKN is required") String tckn
) {
}