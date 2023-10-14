package com.coderiders.gamificationservice.models.dto;

import com.coderiders.gamificationservice.models.enums.ElementType;
import com.coderiders.gamificationservice.models.enums.Tiers;

public record UserPointsDTO(
        String clerkId,
        int points,
        ElementType type,
        Tiers tier,
        int elementId
) {}
