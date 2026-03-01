package com.github.randdd32.donor_search_backend.model.hardware;

import com.github.randdd32.donor_search_backend.model.BaseEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.ManufacturerEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.OpticalDriveFormFactorEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.StorageInterfaceEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "component_optical_drive")
public class OpticalDriveEntity extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(name = "search_name", nullable = false, columnDefinition = "text")
    private String searchName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer_id")
    private ManufacturerEntity manufacturer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_factor_id")
    private OpticalDriveFormFactorEntity formFactor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interface_id")
    private StorageInterfaceEntity storageInterface;

    @OneToMany(mappedBy = "opticalDrive", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OpticalDrivePartNumberEntity> partNumbers = new HashSet<>();
}
