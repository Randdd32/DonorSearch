package com.github.randdd32.donor_search_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "compatibility_rule")
public class CompatibilityRuleEntity extends BaseEntity {
    @Column(name = "rule_code", nullable = false, unique = true, length = 100)
    private String ruleCode;

    @Column(nullable = false, columnDefinition = "text")
    private String expression;

    @Column(name = "error_message", nullable = false, columnDefinition = "text")
    private String errorMessage;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(columnDefinition = "text")
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompatibilityRuleEntity that)) return false;
        return ruleCode != null && ruleCode.equals(that.getRuleCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleCode);
    }
}
