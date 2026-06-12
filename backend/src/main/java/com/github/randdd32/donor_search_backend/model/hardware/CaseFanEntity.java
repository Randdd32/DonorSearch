package com.github.randdd32.donor_search_backend.model.hardware;

import com.github.randdd32.donor_search_backend.model.dictionary.ColorEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.FanConnectorEntity;
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
@Table(name = "component_case_fan")
@DiscriminatorValue("CASE_FAN")
@PrimaryKeyJoinColumn(name = "id")
public class CaseFanEntity extends ComponentEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id")
    private ColorEntity color;

    @Column(name = "size_mm", nullable = false)
    private Integer sizeMm;

    @Column(name = "pwm", nullable = false)
    private Boolean pwm;

    @Column(name = "rpm_min")
    private Integer rpmMin;

    @Column(name = "rpm_max")
    private Integer rpmMax;

    @Column(name = "airflow_min")
    private Integer airflowMin;

    @Column(name = "airflow_max")
    private Integer airflowMax;

    @ManyToMany
    @JoinTable(
            name = "link_case_fan_connector",
            joinColumns = @JoinColumn(name = "case_fan_id"),
            inverseJoinColumns = @JoinColumn(name = "connector_id")
    )
    private Set<FanConnectorEntity> connectors = new HashSet<>();
}
