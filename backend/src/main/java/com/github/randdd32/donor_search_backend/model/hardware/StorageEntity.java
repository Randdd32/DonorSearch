package com.github.randdd32.donor_search_backend.model.hardware;

import com.github.randdd32.donor_search_backend.model.dictionary.ColorEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.StorageFormFactorEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.StorageInterfaceEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.StorageTypeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "component_storage")
@DiscriminatorValue("STORAGE")
@PrimaryKeyJoinColumn(name = "id")
public class StorageEntity extends ComponentEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private StorageTypeEntity storageType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_factor_id")
    private StorageFormFactorEntity formFactor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id")
    private ColorEntity color;

    @Column(name = "is_external", nullable = false)
    private Boolean isExternal;

    @Column(name = "capacity_gb", nullable = false)
    private Integer capacityGb;

    @Column(name = "cache_mb")
    private Integer cacheMb;

    @Column(name = "rpm")
    private Integer rpm;

    @ManyToMany
    @JoinTable(
            name = "link_storage_interface",
            joinColumns = @JoinColumn(name = "storage_id"),
            inverseJoinColumns = @JoinColumn(name = "interface_id")
    )
    private Set<StorageInterfaceEntity> interfaces = new HashSet<>();
}
