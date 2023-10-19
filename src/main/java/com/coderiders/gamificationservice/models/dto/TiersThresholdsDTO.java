package com.coderiders.gamificationservice.models.dto;

import com.coderiders.commonutils.models.enums.BadgeType;


public record TiersThresholdsDTO (
        BadgeType type,
        int lowestTier,
        int lowestThreshold,
        int highestTier,
        int highestThreshold,
        int[] allThresholds
) {}
