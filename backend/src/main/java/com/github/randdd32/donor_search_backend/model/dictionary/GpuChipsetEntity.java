package com.github.randdd32.donor_search_backend.model.dictionary;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "dic_gpu_chipset")
@AttributeOverride(name = "name", column = @Column(name = "name", nullable = false, unique = true, length = 100))
public class GpuChipsetEntity extends NamedDictionaryEntity {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GpuChipsetEntity that)) return false;
        return name != null && name.equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
