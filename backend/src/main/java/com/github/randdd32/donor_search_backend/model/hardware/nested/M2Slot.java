package com.github.randdd32.donor_search_backend.model.hardware.nested;

import java.io.Serializable;
import java.util.List;

public record M2Slot(
        List<Integer> sizes,
        String keys
) implements Serializable {}
