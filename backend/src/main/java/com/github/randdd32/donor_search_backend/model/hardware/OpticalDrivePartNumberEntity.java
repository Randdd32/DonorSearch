package com.github.randdd32.donor_search_backend.model.hardware;

import com.github.randdd32.donor_search_backend.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "link_optical_drive_part_number")
public class OpticalDrivePartNumberEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "optical_drive_id", nullable = false)
    private OpticalDriveEntity opticalDrive;

    @Column(name = "part_number", nullable = false, unique = true, length = 100)
    private String partNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpticalDrivePartNumberEntity that)) return false;
        return partNumber != null && partNumber.equals(that.getPartNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(partNumber);
    }
}
