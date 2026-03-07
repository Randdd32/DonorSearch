package com.github.randdd32.donor_search_backend.model.hardware;

import com.github.randdd32.donor_search_backend.model.dictionary.ColorEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.ExpansionInterfaceEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.GpuChipsetEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.MemoryTypeEntity;
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

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "component_video_card")
@DiscriminatorValue("VIDEO_CARD")
@PrimaryKeyJoinColumn(name = "id")
public class VideoCardEntity extends ComponentEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chipset_id")
    private GpuChipsetEntity chipset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memory_type_id")
    private MemoryTypeEntity memoryType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interface_id")
    private ExpansionInterfaceEntity interfaceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id")
    private ColorEntity color;

    @Column(name = "memory_gb", nullable = false)
    private Integer memoryGb;

    @Column(name = "length_mm")
    private Integer lengthMm;

    @Column(name = "tdp_w", nullable = false)
    private Integer tdpW;

    @Column(name = "slot_width", nullable = false)
    private Integer slotWidth;

    @Column(name = "case_expansion_width", nullable = false)
    private Integer caseExpansionWidth;

    @Column(name = "power_6pin_count", nullable = false)
    private Integer power6pinCount;

    @Column(name = "power_8pin_count", nullable = false)
    private Integer power8pinCount;

    @Column(name = "power_12pin_count", nullable = false)
    private Integer power12pinCount;

    @Column(name = "power_12vhpwr_count", nullable = false)
    private Integer power12vhpwrCount;

    @Column(name = "power_eps_count", nullable = false)
    private Integer powerEpsCount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "video_outputs", nullable = false, columnDefinition = "jsonb")
    private Map<String, Integer> videoOutputs;
}
