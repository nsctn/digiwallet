package ecetin.digiwallet.hub.employee.interfaces.dto;

import ecetin.digiwallet.hub.employee.domain.Employee;

import java.util.UUID;

public record EmployeeResponse(
    UUID id,
    String name,
    String surname,
    String employeeId
) {
    public static EmployeeResponse fromEmployee(Employee employee) {
        return new EmployeeResponse(
            employee.getId(),
            employee.getName(),
            employee.getSurname(),
            employee.getEmployeeId()
        );
    }
}