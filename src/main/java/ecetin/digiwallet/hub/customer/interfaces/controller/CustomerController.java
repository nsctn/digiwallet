package ecetin.digiwallet.hub.customer.interfaces.controller;

import ecetin.digiwallet.hub.common.security.CustomerId;
import ecetin.digiwallet.hub.common.security.IsEmployee;
import ecetin.digiwallet.hub.customer.application.CustomerService;
import ecetin.digiwallet.hub.customer.domain.Customer;
import ecetin.digiwallet.hub.customer.interfaces.dto.CreateCustomerRequest;
import ecetin.digiwallet.hub.customer.interfaces.dto.CustomerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER:VIEW')")
    public ResponseEntity<CustomerResponse> getCustomerById(
            @CustomerId UUID authenticatedCustomerId,
            @IsEmployee boolean isEmployee,
            @PathVariable UUID id) {
        
        Optional<Customer> customer = customerService.findById(authenticatedCustomerId, id, isEmployee);
        return customer
                .map(c -> ResponseEntity.ok(CustomerResponse.fromCustomer(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tckn/{tckn}")
    @PreAuthorize("hasRole('CUSTOMER:VIEW')")
    public ResponseEntity<CustomerResponse> getCustomerByTckn(
            @IsEmployee boolean isEmployee,
            @PathVariable String tckn) {
        
        Optional<Customer> customer = customerService.findByTckn(tckn, isEmployee);
        return customer
                .map(c -> ResponseEntity.ok(CustomerResponse.fromCustomer(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER:CREATE')")
    public ResponseEntity<CustomerResponse> createCustomer(
            @IsEmployee boolean isEmployee,
            @Valid @RequestBody CreateCustomerRequest request) {
        
        Customer customer = customerService.createCustomer(
                request.name(),
                request.surname(),
                request.tckn(),
                isEmployee
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomerResponse.fromCustomer(customer));
    }
}