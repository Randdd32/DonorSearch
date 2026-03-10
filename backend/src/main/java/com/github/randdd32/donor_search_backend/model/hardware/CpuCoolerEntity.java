package com.github.randdd32.donor_search_backend.model.hardware;

import com.github.randdd32.donor_search_backend.model.dictionary.ColorEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.CpuSocketEntity;
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
@Table(name = "component_cpu_cooler")
@DiscriminatorValue("CPU_COOLER")
@PrimaryKeyJoinColumn(name = "id")
public class CpuCoolerEntity extends ComponentEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id")
    private ColorEntity color;

    @Column(name = "is_water_cooled", nullable = false)
    private Boolean isWaterCooled;

    @Column(name = "height_mm")
    private Integer heightMm;

    @Column(name = "water_cooled_size_mm")
    private Integer waterCooledSizeMm;

    @Column(name = "rpm_min")
    private Integer rpmMin;

    @Column(name = "rpm_max")
    private Integer rpmMax;

    @ManyToMany
    @JoinTable(
            name = "link_cpu_cooler_socket",
            joinColumns = @JoinColumn(name = "cpu_cooler_id"),
            inverseJoinColumns = @JoinColumn(name = "socket_id")
    )
    private Set<CpuSocketEntity> sockets = new HashSet<>();
}
