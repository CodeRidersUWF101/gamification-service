package com.coderiders.gamificationservice.models.commonutils.models.records;

import com.coderiders.gamificationservice.models.commonutils.models.enums.BadgeType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BadgeWithNext  {
    private Long id;
    private String name;
    private String description;
    private Integer threshold;
    private BadgeType type;
    private Short tier;
    private String imageUrl;
    private Integer pointsAwarded;
    private Badge nextBadge;
}
