package ecetin.digiwallet.hub.employee.interfaces.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateEmployeeRequest(
    @NotBlank(message = "Name is required") String name,
    @NotBlank(message = "Surname is required") String surname,
    @NotBlank(message = "Employee ID is required") String employeeId
) {
}