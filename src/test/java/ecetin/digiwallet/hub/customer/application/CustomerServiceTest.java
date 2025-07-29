package ecetin.digiwallet.hub.customer.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_DEFAULTS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import ecetin.digiwallet.hub.customer.domain.Customer;
import ecetin.digiwallet.hub.customer.infrastructure.CustomerRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private UUID customerId;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        // Create a mock Customer instead of a real one
        customer = mock(Customer.class, RETURNS_DEFAULTS);
        // Use lenient() to avoid UnnecessaryStubbingException
        lenient().when(customer.getId()).thenReturn(customerId);
        lenient().when(customer.getName()).thenReturn("John");
        lenient().when(customer.getSurname()).thenReturn("Doe");
        lenient().when(customer.getTckn()).thenReturn("12345678901");
    }

    @Test
    void findById_WhenUserIsEmployee_ShouldReturnCustomer() {
        // Arrange
        UUID authenticatedCustomerId = UUID.randomUUID(); // Different from the target customer
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // Act
        Optional<Customer> result = customerService.findById(authenticatedCustomerId, customerId, true);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(customer, result.get());
        verify(customerRepository).findById(customerId);
    }

    @Test
    void findById_WhenUserIsCustomerAccessingOwnData_ShouldReturnCustomer() {
        // Arrange
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // Act
        Optional<Customer> result = customerService.findById(customerId, customerId, false);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(customer, result.get());
        verify(customerRepository).findById(customerId);
    }

    @Test
    void findById_WhenUserIsCustomerAccessingOtherCustomerData_ShouldThrowAccessDeniedException() {
        // Arrange
        UUID authenticatedCustomerId = UUID.randomUUID(); // Different from the target customer

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            customerService.findById(authenticatedCustomerId, customerId, false);
        });
        verify(customerRepository, never()).findById(any());
    }

    @Test
    void findByTckn_WhenUserIsEmployee_ShouldReturnCustomer() {
        // Arrange
        String tckn = "12345678901";
        when(customerRepository.findByTckn(tckn)).thenReturn(Optional.of(customer));

        // Act
        Optional<Customer> result = customerService.findByTckn(tckn, true);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(customer, result.get());
        verify(customerRepository).findByTckn(tckn);
    }

    @Test
    void findByTckn_WhenUserIsNotEmployee_ShouldThrowAccessDeniedException() {
        // Arrange
        String tckn = "12345678901";

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            customerService.findByTckn(tckn, false);
        });
        verify(customerRepository, never()).findByTckn(any());
    }

    @Test
    void createCustomer_WhenUserIsEmployee_ShouldCreateAndReturnCustomer() {
        // Arrange
        String name = "Jane";
        String surname = "Smith";
        String tckn = "98765432109";
        Customer newCustomer = mock(Customer.class);
        when(newCustomer.getName()).thenReturn(name);
        when(newCustomer.getSurname()).thenReturn(surname);
        when(newCustomer.getTckn()).thenReturn(tckn);
        when(customerRepository.save(any(Customer.class))).thenReturn(newCustomer);

        // Act
        Customer result = customerService.createCustomer(name, surname, tckn, true);

        // Assert
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(surname, result.getSurname());
        assertEquals(tckn, result.getTckn());
        verify(customerRepository).save(any(Customer.class));
        // Verify that registerCustomerCreatedEvent is called after saving
        verify(newCustomer).registerCustomerCreatedEvent();
    }

    @Test
    void createCustomer_WhenUserIsNotEmployee_ShouldThrowAccessDeniedException() {
        // Arrange
        String name = "Jane";
        String surname = "Smith";
        String tckn = "98765432109";

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            customerService.createCustomer(name, surname, tckn, false);
        });
        verify(customerRepository, never()).save(any(Customer.class));
    }
}
