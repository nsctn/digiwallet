package ecetin.digiwallet.hub.employee.domain;

import ecetin.digiwallet.hub.common.model.BaseAggregateRoot;
import ecetin.digiwallet.hub.employee.domain.event.EmployeeCreatedEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(name = "EMPLOYEE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Employee extends BaseAggregateRoot<UUID> {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false, unique = true)
    private String employeeId;

    public Employee(String name, String surname, String employeeId) {
        this.name = name;
        this.surname = surname;
        this.employeeId = employeeId;
        
        // Register the employee created event
        this.registerEvent(new EmployeeCreatedEvent(this.getId(), name, surname, employeeId));
    }
}