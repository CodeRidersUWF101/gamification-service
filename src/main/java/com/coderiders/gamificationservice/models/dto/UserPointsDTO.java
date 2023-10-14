package com.coderiders.gamificationservice.models.dto;

import com.coderiders.gamificationservice.models.enums.ElementType;

public record UserPointsDTO(
        String clerkId,
        int points,
        ElementType type,
        short tier,
        int elementId
) {}
