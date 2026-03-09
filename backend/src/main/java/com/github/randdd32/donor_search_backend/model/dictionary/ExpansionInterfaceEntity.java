package com.github.randdd32.donor_search_backend.model.dictionary;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "dic_expansion_interface")
public class ExpansionInterfaceEntity extends NamedDictionaryEntity {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpansionInterfaceEntity that)) return false;
        return name != null && name.equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
