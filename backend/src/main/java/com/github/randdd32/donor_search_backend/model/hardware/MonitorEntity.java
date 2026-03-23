package com.github.randdd32.donor_search_backend.model.hardware;

import com.github.randdd32.donor_search_backend.model.dictionary.AspectRatioEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.MonitorResolutionEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.PanelTypeEntity;
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
@Table(name = "component_monitor")
@DiscriminatorValue("MONITOR")
@PrimaryKeyJoinColumn(name = "id")
public class MonitorEntity extends ComponentEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolution_id")
    private MonitorResolutionEntity resolution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "panel_type_id")
    private PanelTypeEntity panelType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aspect_ratio_id")
    private AspectRatioEntity aspectRatio;

    @Column(name = "screen_size_in", nullable = false)
    private Double screenSizeIn;

    @Column(name = "refresh_rate_hz")
    private Integer refreshRateHz;

    @Column(name = "response_time_ms")
    private Double responseTimeMs;

    @Column(name = "input_hdmi", nullable = false)
    private Integer inputHdmi;

    @Column(name = "input_dp", nullable = false)
    private Integer inputDp;

    @Column(name = "input_dvi", nullable = false)
    private Integer inputDvi;

    @Column(name = "input_vga", nullable = false)
    private Integer inputVga;

    @Column(name = "input_usb_c", nullable = false)
    private Integer inputUsbC;

    @Column(name = "input_mini_hdmi", nullable = false)
    private Integer inputMiniHdmi;

    @Column(name = "input_micro_hdmi", nullable = false)
    private Integer inputMicroHdmi;

    @Column(name = "input_mini_dp", nullable = false)
    private Integer inputMiniDp;

    @Column(name = "input_bnc", nullable = false)
    private Integer inputBnc;

    @Column(name = "input_component", nullable = false)
    private Integer inputComponent;

    @Column(name = "input_s_video", nullable = false)
    private Integer inputSVideo;

    @Column(name = "input_virtual_link", nullable = false)
    private Integer inputVirtualLink;
}
