package ecetin.digiwallet.hub.common.model;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

@MappedSuperclass
public abstract class BaseAggregateRoot<Id extends Serializable> extends BaseEntity<Id> {

    @Transient
    private final List<Object> domainEvents = new CopyOnWriteArrayList<>();

    protected void registerEvent(Object event) {
        Objects.requireNonNull(event, "Event must not be null");
        domainEvents.add(event);
    }

    @AfterDomainEventPublication
    protected void clearDomainEvents() {
        this.domainEvents.clear();
    }

    @DomainEvents
    protected Collection<Object> domainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

}