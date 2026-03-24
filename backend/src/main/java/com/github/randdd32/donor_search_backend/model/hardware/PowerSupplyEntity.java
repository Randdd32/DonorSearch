package com.github.randdd32.donor_search_backend.model.hardware;

import com.github.randdd32.donor_search_backend.model.dictionary.ColorEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.EfficiencyRatingEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.ModularTypeEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.PowerSupplyTypeEntity;
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
@Table(name = "component_power_supply")
@DiscriminatorValue("POWER_SUPPLY")
@PrimaryKeyJoinColumn(name = "id")
public class PowerSupplyEntity extends ComponentEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private PowerSupplyTypeEntity powerSupplyType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "efficiency_id")
    private EfficiencyRatingEntity efficiency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modular_id")
    private ModularTypeEntity modular;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id")
    private ColorEntity color;

    @Column(name = "wattage_w", nullable = false)
    private Integer wattageW;

    @Column(name = "length_mm")
    private Integer lengthMm;

    @Column(name = "atx_4pin_connectors", nullable = false)
    private Integer atx4PinConnectors;

    @Column(name = "eps_8pin_connectors", nullable = false)
    private Integer eps8PinConnectors;

    @Column(name = "pcie_12vhpwr_connectors", nullable = false)
    private Integer pcie12vhpwrConnectors;

    @Column(name = "pcie_12pin_connectors", nullable = false)
    private Integer pcie12PinConnectors;

    @Column(name = "pcie_8pin_connectors", nullable = false)
    private Integer pcie8PinConnectors;

    @Column(name = "pcie_6plus2pin_connectors", nullable = false)
    private Integer pcie6Plus2PinConnectors;

    @Column(name = "pcie_6pin_connectors", nullable = false)
    private Integer pcie6PinConnectors;

    @Column(name = "sata_connectors", nullable = false)
    private Integer sataConnectors;

    @Column(name = "molex_4pin_connectors", nullable = false)
    private Integer molex4PinConnectors;
}
