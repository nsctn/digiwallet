package ecetin.digiwallet.hub.customer.domain;

import ecetin.digiwallet.hub.customer.infrastructure.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testTableCreation() {
        // Create a new customer without setting the ID (let JPA generate it)
        Customer customer = new Customer("John", "Doe", "12345678901");

        // Save the customer
        Customer savedCustomer = customerRepository.save(customer);
        UUID customerId = savedCustomer.getId();

        // Retrieve the customer by ID
        assertNotNull(customerId);
        Optional<Customer> retrievedCustomer = customerRepository.findById(customerId);

        // Assert that the customer was saved and retrieved successfully
        assertTrue(retrievedCustomer.isPresent());
        assertEquals("John", retrievedCustomer.get().getName());
        assertEquals("Doe", retrievedCustomer.get().getSurname());
        assertEquals("12345678901", retrievedCustomer.get().getTckn());

        // Test the custom findByTckn method
        Optional<Customer> customerByTckn = customerRepository.findByTckn("12345678901");
        assertTrue(customerByTckn.isPresent());
        assertEquals(customerId, customerByTckn.get().getId());
    }
}