package ecetin.digiwallet.hub.common.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity<Id extends Serializable> extends AbstractPersistable<Id> {

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  private Instant updatedAt;

  @CreatedBy
  @Column(updatable = false)
  private Id createdBy;

  @LastModifiedBy
  private Id updatedBy;

  @Enumerated(EnumType.STRING)
  @Column(name = "entity_status", nullable = false)
  private EntityStatus entityStatus;

  @Enumerated(EnumType.STRING)
  @Column(name = "LAST_OPERATION")
  private EntityOperation lastOperation;

  @PrePersist
  public void onPrePersist() {
    this.setLastOperation(EntityOperation.ADD);
    this.setEntityStatus(EntityStatus.ACTIVE);
  }

  @PreUpdate
  public void onPreUpdate() {
    this.setLastOperation(EntityOperation.UPDATE);
  }

  @PreRemove
  public void onPreRemove() {
    this.setLastOperation(EntityOperation.REMOVE);
  }

  public boolean isActive() {
    return this.getEntityStatus() == EntityStatus.ACTIVE;
  }

}