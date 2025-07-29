package ecetin.digiwallet.hub.employee.application;

import ecetin.digiwallet.hub.employee.domain.Employee;
import ecetin.digiwallet.hub.employee.infrastructure.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public Optional<Employee> findById(UUID id) {
        return employeeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Employee> findByEmployeeId(String employeeId) {
        return employeeRepository.findByEmployeeId(employeeId);
    }

    @Transactional
    public Employee createEmployee(String name, String surname, String employeeId) {
        Employee employee = new Employee(name, surname, employeeId);
        return employeeRepository.save(employee);
    }
}