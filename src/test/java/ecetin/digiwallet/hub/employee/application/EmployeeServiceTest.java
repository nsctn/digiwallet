package ecetin.digiwallet.hub.employee.application;

import ecetin.digiwallet.hub.employee.domain.Employee;
import ecetin.digiwallet.hub.employee.infrastructure.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private UUID employeeId;
    private Employee employee;
    private String employeeIdString;

    @BeforeEach
    void setUp() {
        employeeId = UUID.randomUUID();
        employeeIdString = "EMP12345";
        // Create a mock Employee instead of a real one
        employee = mock(Employee.class, RETURNS_DEFAULTS);
        // Use lenient() to avoid UnnecessaryStubbingException
        lenient().when(employee.getId()).thenReturn(employeeId);
        lenient().when(employee.getName()).thenReturn("John");
        lenient().when(employee.getSurname()).thenReturn("Doe");
        lenient().when(employee.getEmployeeId()).thenReturn(employeeIdString);
    }

    @Test
    void findById_ShouldReturnEmployee() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        // Act
        Optional<Employee> result = employeeService.findById(employeeId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(employee, result.get());
        verify(employeeRepository).findById(employeeId);
    }

    @Test
    void findById_WhenEmployeeDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act
        Optional<Employee> result = employeeService.findById(employeeId);

        // Assert
        assertFalse(result.isPresent());
        verify(employeeRepository).findById(employeeId);
    }

    @Test
    void findByEmployeeId_ShouldReturnEmployee() {
        // Arrange
        when(employeeRepository.findByEmployeeId(employeeIdString)).thenReturn(Optional.of(employee));

        // Act
        Optional<Employee> result = employeeService.findByEmployeeId(employeeIdString);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(employee, result.get());
        verify(employeeRepository).findByEmployeeId(employeeIdString);
    }

    @Test
    void findByEmployeeId_WhenEmployeeDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(employeeRepository.findByEmployeeId(employeeIdString)).thenReturn(Optional.empty());

        // Act
        Optional<Employee> result = employeeService.findByEmployeeId(employeeIdString);

        // Assert
        assertFalse(result.isPresent());
        verify(employeeRepository).findByEmployeeId(employeeIdString);
    }

    @Test
    void createEmployee_ShouldCreateAndReturnEmployee() {
        // Arrange
        String name = "Jane";
        String surname = "Smith";
        String empId = "EMP67890";
        Employee newEmployee = mock(Employee.class);
        when(newEmployee.getName()).thenReturn(name);
        when(newEmployee.getSurname()).thenReturn(surname);
        when(newEmployee.getEmployeeId()).thenReturn(empId);
        when(employeeRepository.save(any(Employee.class))).thenReturn(newEmployee);

        // Act
        Employee result = employeeService.createEmployee(name, surname, empId);

        // Assert
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(surname, result.getSurname());
        assertEquals(empId, result.getEmployeeId());
        verify(employeeRepository).save(any(Employee.class));
        // Verify that registerEmployeeCreatedEvent is called after saving
        verify(newEmployee).registerEmployeeCreatedEvent();
    }
}
