package ecetin.digiwallet.hub.customer.application;

import ecetin.digiwallet.hub.customer.domain.Customer;
import ecetin.digiwallet.hub.customer.infrastructure.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * Finds a customer by ID with access control check.
     *
     * @param authenticatedCustomerId The ID of the authenticated customer
     * @param id The ID of the customer to find
     * @param isEmployee Whether the authenticated user is an employee
     * @return An Optional containing the customer if found and accessible
     */
    @Transactional(readOnly = true)
    public Optional<Customer> findById(UUID authenticatedCustomerId, UUID id, boolean isEmployee) {
        // Check if the user is allowed to access this customer
        checkCustomerAccess(authenticatedCustomerId, id, isEmployee);
        
        return customerRepository.findById(id);
    }

    /**
     * Finds a customer by TCKN with access control check.
     * Note: This method doesn't check if the authenticated customer is accessing their own data
     * because we don't know the customer ID before the lookup. The controller should perform
     * an additional check after retrieving the customer.
     *
     * @param tckn The TCKN of the customer to find
     * @param isEmployee Whether the authenticated user is an employee
     * @return An Optional containing the customer if found
     */
    @Transactional(readOnly = true)
    public Optional<Customer> findByTckn(String tckn, boolean isEmployee) {
        // Only employees can look up customers by TCKN
        if (!isEmployee) {
            throw new AccessDeniedException("Only employees can look up customers by TCKN");
        }
        
        return customerRepository.findByTckn(tckn);
    }

    /**
     * Creates a new customer.
     * Note: Only employees can create customers, so this method should only be called
     * after verifying that the authenticated user is an employee.
     *
     * @param name The name of the customer
     * @param surname The surname of the customer
     * @param tckn The TCKN of the customer
     * @param isEmployee Whether the authenticated user is an employee
     * @return The created customer
     */
    @Transactional
    public Customer createCustomer(String name, String surname, String tckn, boolean isEmployee) {
        // Only employees can create customers
        if (!isEmployee) {
            throw new AccessDeniedException("Only employees can create customers");
        }
        
        Customer customer = new Customer(name, surname, tckn);
        return customerRepository.save(customer);
    }
    
    /**
     * Checks if the authenticated user has access to the specified customer's data.
     *
     * @param authenticatedCustomerId The ID of the authenticated customer
     * @param targetCustomerId The ID of the target customer
     * @param isEmployee Whether the authenticated user is an employee
     */
    private void checkCustomerAccess(UUID authenticatedCustomerId, UUID targetCustomerId, boolean isEmployee) {
        // If user is an employee, they can access any customer's data
        if (isEmployee) {
            return;
        }
        
        // Otherwise, check if the user is accessing their own data
        if (authenticatedCustomerId == null || !authenticatedCustomerId.equals(targetCustomerId)) {
            throw new AccessDeniedException("You can only access your own data");
        }
    }
}