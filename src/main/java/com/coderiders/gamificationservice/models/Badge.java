package com.coderiders.gamificationservice.models;

import com.coderiders.gamificationservice.models.enums.BadgeType;

public record Badge (
        long id,
        String name,
        String description,
        int threshold,
        BadgeType type,
        short tier,
        String imageUrl,
        int pointsAwarded
) {}
