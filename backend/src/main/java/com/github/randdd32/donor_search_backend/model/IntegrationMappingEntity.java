package com.github.randdd32.donor_search_backend.model;

import com.github.randdd32.donor_search_backend.model.enums.MappingConfidence;
import com.github.randdd32.donor_search_backend.model.hardware.ComponentEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "integration_mapping")
@EntityListeners(AuditingEntityListener.class)
public class IntegrationMappingEntity extends BaseEntity {
    @Column(name = "external_name", nullable = false, unique = true, columnDefinition = "text")
    private String externalName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "internal_component_id", nullable = false)
    private ComponentEntity internalComponent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MappingConfidence confidence;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntegrationMappingEntity that)) return false;
        return externalName != null && externalName.equals(that.getExternalName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalName);
    }
}
