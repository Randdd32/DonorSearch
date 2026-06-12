package com.github.randdd32.donor_search_backend.model.dictionary;

import com.github.randdd32.donor_search_backend.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class NamedDictionaryEntity extends BaseEntity {
    @Column(nullable = false, unique = true, length = 50)
    protected String name;
}
