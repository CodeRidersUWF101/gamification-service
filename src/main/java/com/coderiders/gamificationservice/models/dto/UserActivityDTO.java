package com.coderiders.gamificationservice.models.dto;

import com.coderiders.gamificationservice.models.enums.ActivityAction;

public record UserActivityDTO(
        String clerkId,
        ActivityAction action,
        Integer actionId
) {}
