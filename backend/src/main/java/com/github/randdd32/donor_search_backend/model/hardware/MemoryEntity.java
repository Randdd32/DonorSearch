package com.github.randdd32.donor_search_backend.model.hardware;

import com.github.randdd32.donor_search_backend.model.dictionary.ColorEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.MemoryTypeEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.RamFormFactorEntity;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "component_memory")
@DiscriminatorValue("MEMORY")
@PrimaryKeyJoinColumn(name = "id")
public class MemoryEntity extends ComponentEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_factor_id")
    private RamFormFactorEntity formFactor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memory_type_id")
    private MemoryTypeEntity memoryType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id")
    private ColorEntity color;

    @Column(name = "cas_latency", nullable = false)
    private Integer casLatency;

    @Column(name = "frequency_mhz")
    private Integer frequencyMhz;

    @Column(name = "modules_count", nullable = false)
    private Integer modulesCount;

    @Column(name = "modules_size_gb", nullable = false)
    private Integer modulesSizeGb;

    @Column(name = "is_ecc", nullable = false)
    private Boolean isEcc;

    @Column(name = "is_registered", nullable = false)
    private Boolean isRegistered;
}
