package ecetin.digiwallet.hub.employee.interfaces.controller;

import ecetin.digiwallet.hub.employee.application.EmployeeService;
import ecetin.digiwallet.hub.employee.domain.Employee;
import ecetin.digiwallet.hub.employee.interfaces.dto.CreateEmployeeRequest;
import ecetin.digiwallet.hub.employee.interfaces.dto.EmployeeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE:VIEW')")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable UUID id) {
        Optional<Employee> employee = employeeService.findById(id);
        return employee
                .map(e -> ResponseEntity.ok(EmployeeResponse.fromEmployee(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/employeeId/{employeeId}")
    @PreAuthorize("hasRole('EMPLOYEE:VIEW')")
    public ResponseEntity<EmployeeResponse> getEmployeeByEmployeeId(@PathVariable String employeeId) {
        Optional<Employee> employee = employeeService.findByEmployeeId(employeeId);
        return employee
                .map(e -> ResponseEntity.ok(EmployeeResponse.fromEmployee(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE:CREATE')")
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        Employee employee = employeeService.createEmployee(
                request.name(),
                request.surname(),
                request.employeeId()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EmployeeResponse.fromEmployee(employee));
    }
}