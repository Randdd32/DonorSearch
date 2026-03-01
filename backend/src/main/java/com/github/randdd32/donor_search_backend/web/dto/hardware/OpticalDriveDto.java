package com.github.randdd32.donor_search_backend.web.dto.hardware;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OpticalDriveDto {
    private Long id;
    private String name;
    private String searchName;

    private String manufacturerName;
    private String formFactorName;
    private String interfaceName;

    private List<String> partNumbers;
}
