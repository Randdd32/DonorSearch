package com.github.randdd32.donor_search_backend.model.dictionary;

import com.github.randdd32.donor_search_backend.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class NamedDictionaryEntity extends BaseEntity {
    @Column(nullable = false, unique = true, length = 50)
    protected String name;
}
