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
public class Badge {
        Long id;
        String name;
        String description;
        Integer threshold;
        BadgeType type;
        Short tier;
        String imageUrl;
        Integer pointsAwarded;
}
