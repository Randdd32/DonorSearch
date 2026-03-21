package com.github.randdd32.donor_search_backend.model.hardware;

import com.github.randdd32.donor_search_backend.model.dictionary.AudioChipsetEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.ColorEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.ExpansionInterfaceEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.WirelessProtocolEntity;
import com.github.randdd32.donor_search_backend.model.enums.ExpansionCardType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "component_expansion_card")
@DiscriminatorValue("EXPANSION_CARD")
@PrimaryKeyJoinColumn(name = "id")
public class ExpansionCardEntity extends ComponentEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false)
    private ExpansionCardType cardType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interface_id")
    private ExpansionInterfaceEntity interfaceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id")
    private ColorEntity color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audio_chipset_id")
    private AudioChipsetEntity audioChipset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protocol_id")
    private WirelessProtocolEntity protocol;

    @Column(name = "channels")
    private Double channels;

    @Column(name = "digital_audio_bit")
    private Integer digitalAudioBit;

    @Column(name = "sample_rate_khz")
    private Double sampleRateKhz;
}
