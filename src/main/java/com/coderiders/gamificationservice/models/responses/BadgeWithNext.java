package com.coderiders.gamificationservice.models.responses;

import com.coderiders.gamificationservice.models.Badge;
import com.coderiders.gamificationservice.models.enums.BadgeType;

public record BadgeWithNext (
        long id,
        String name,
        String description,
        int threshold,
        BadgeType type,
        short tier,
        String imageUrl,
        int pointsAwarded,
        Badge nextBadge
) {
}
