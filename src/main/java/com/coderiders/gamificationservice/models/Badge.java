package com.coderiders.gamificationservice.models;

import com.coderiders.gamificationservice.models.enums.BadgeType;
import com.coderiders.gamificationservice.models.enums.Tiers;

public record Badge (
        long id,
        String name,
        String description,
        int threshold,
        BadgeType type,
        Tiers tier,
        int tierNumber,
        String imageUrl,
        int pointsAwarded
) {}
