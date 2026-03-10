package com.github.randdd32.donor_search_backend.model.hardware;

import com.github.randdd32.donor_search_backend.model.dictionary.CpuSocketEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.IntegratedGraphicsEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.MicroarchitectureEntity;
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
@Table(name = "component_cpu")
@DiscriminatorValue("CPU")
@PrimaryKeyJoinColumn(name = "id")
public class CpuEntity extends ComponentEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "socket_id")
    private CpuSocketEntity socket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "microarchitecture_id")
    private MicroarchitectureEntity microarchitecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graphics_id")
    private IntegratedGraphicsEntity graphics;

    @Column(name = "core_count", nullable = false)
    private Integer coreCount;

    @Column(name = "core_clock_ghz", nullable = false)
    private Double coreClockGhz;

    @Column(name = "boost_clock_ghz")
    private Double boostClockGhz;

    @Column(name = "tdp_w", nullable = false)
    private Integer tdpW;

    @Column(name = "max_memory_gb")
    private Integer maxMemoryGb;

    @Column(name = "ecc_support", nullable = false)
    private Boolean eccSupport;
}
