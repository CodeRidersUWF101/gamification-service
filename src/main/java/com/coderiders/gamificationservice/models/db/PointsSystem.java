package com.coderiders.gamificationservice.models.db;

import com.coderiders.gamificationservice.models.enums.ElementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointsSystem {

    private long id;
    private ElementType elementType;
    private short tier;
    private int pointsAwarded;
}
