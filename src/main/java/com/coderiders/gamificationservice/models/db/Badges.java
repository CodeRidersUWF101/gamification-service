package com.coderiders.gamificationservice.models.db;

import com.coderiders.gamificationservice.models.commonutils.models.enums.BadgeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Badges {
    private long id;
    private String name;
    private String description;
    private int threshold;
    private BadgeType type;
    private short tier;
    private String imageUrl;
}
