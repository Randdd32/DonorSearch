package com.github.randdd32.donor_search_backend.model.hardware;

import com.github.randdd32.donor_search_backend.model.dictionary.ColorEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.CpuSocketEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.MemoryTypeEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.MotherboardFormFactorEntity;
import com.github.randdd32.donor_search_backend.model.hardware.nested.M2Slot;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "component_motherboard")
@DiscriminatorValue("MOTHERBOARD")
@PrimaryKeyJoinColumn(name = "id")
public class MotherboardEntity extends ComponentEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "socket_id")
    private CpuSocketEntity socket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_factor_id")
    private MotherboardFormFactorEntity formFactor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memory_type_id")
    private MemoryTypeEntity memoryType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id")
    private ColorEntity color;

    @Column(name = "max_memory_gb", nullable = false)
    private Integer maxMemoryGb;

    @Column(name = "memory_slots", nullable = false)
    private Integer memorySlots;

    @Column(name = "memory_speed_max_mhz", nullable = false)
    private Integer memorySpeedMaxMhz;

    @Column(name = "ecc_support", nullable = false)
    private Boolean eccSupport;

    @Column(name = "uses_back_connect", nullable = false)
    private Boolean usesBackConnect;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "m2_slots", nullable = false, columnDefinition = "jsonb")
    private List<M2Slot> m2Slots;

    @Column(name = "sata_6_ports", nullable = false)
    private Integer sata6Ports;

    @Column(name = "sata_3_ports", nullable = false)
    private Integer sata3Ports;

    @Column(name = "pci_x16_slots", nullable = false)
    private Integer pciX16Slots;

    @Column(name = "pci_x8_slots", nullable = false)
    private Integer pciX8Slots;

    @Column(name = "pci_x4_slots", nullable = false)
    private Integer pciX4Slots;

    @Column(name = "pci_x1_slots", nullable = false)
    private Integer pciX1Slots;

    @Column(name = "pci_slots", nullable = false)
    private Integer pciSlots;

    @Column(name = "mini_pcie_msata_slots", nullable = false)
    private Integer miniPcieMsataSlots;

    @Column(name = "header_usb_2_0", nullable = false)
    private Integer headerUsb20;

    @Column(name = "header_usb_3_2_gen_1", nullable = false)
    private Integer headerUsb32Gen1;

    @Column(name = "header_usb_3_2_gen_2", nullable = false)
    private Integer headerUsb32Gen2;

    @Column(name = "header_usb_3_2_gen_2x2", nullable = false)
    private Integer headerUsb32Gen2x2;

    @Column(name = "header_usb_2_0_single_port", nullable = false)
    private Integer headerUsb20SinglePort;
}
