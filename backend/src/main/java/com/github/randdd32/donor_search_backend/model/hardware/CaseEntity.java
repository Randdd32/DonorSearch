package com.github.randdd32.donor_search_backend.model.hardware;

import com.github.randdd32.donor_search_backend.model.dictionary.CaseTypeEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.ColorEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.FrontPanelUsbEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.MotherboardFormFactorEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.SidePanelEntity;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "component_case")
@DiscriminatorValue("CASE")
@PrimaryKeyJoinColumn(name = "id")
public class CaseEntity extends ComponentEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private CaseTypeEntity caseType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id")
    private ColorEntity color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "side_panel_id")
    private SidePanelEntity sidePanel;

    @Column(name = "length_mm")
    private Integer lengthMm;

    @Column(name = "width_mm")
    private Integer widthMm;

    @Column(name = "height_mm")
    private Integer heightMm;

    @Column(name = "max_gpu_len_mm")
    private Integer maxGpuLenMm;

    @Column(name = "max_cpu_cooler_height_mm")
    private Integer maxCpuCoolerHeightMm;

    @Column(name = "int_35_bays", nullable = false)
    private Integer int35Bays;

    @Column(name = "ext_525_bays", nullable = false)
    private Integer ext525Bays;

    @Column(name = "ext_35_bays", nullable = false)
    private Integer ext35Bays;

    @Column(name = "int_25_bays", nullable = false)
    private Integer int25Bays;

    @Column(name = "expansion_slots_full_height", nullable = false)
    private Integer expansionSlotsFullHeight;

    @Column(name = "expansion_slots_half_height", nullable = false)
    private Integer expansionSlotsHalfHeight;

    @Column(name = "expansion_slots_riser", nullable = false)
    private Integer expansionSlotsRiser;

    @Column(name = "radiator_support_text", length = 500)
    private String radiatorSupportText;

    @Column(name = "fan_support_text", length = 500)
    private String fanSupportText;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "radiator_sizes", nullable = false, columnDefinition = "jsonb")
    private List<Integer> radiatorSizes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "fan_sizes", nullable = false, columnDefinition = "jsonb")
    private List<Integer> fanSizes;

    @ManyToMany
    @JoinTable(
            name = "link_case_mobo_form_factor",
            joinColumns = @JoinColumn(name = "case_id"),
            inverseJoinColumns = @JoinColumn(name = "form_factor_id")
    )
    private Set<MotherboardFormFactorEntity> moboFormFactors = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "link_case_front_panel_usb",
            joinColumns = @JoinColumn(name = "case_id"),
            inverseJoinColumns = @JoinColumn(name = "usb_id")
    )
    private Set<FrontPanelUsbEntity> frontPanelUsbTypes = new HashSet<>();
}
