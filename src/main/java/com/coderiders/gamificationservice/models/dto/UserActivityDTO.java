package com.coderiders.gamificationservice.models.dto;


import com.coderiders.commonutils.models.enums.ActivityAction;

public record UserActivityDTO(
        String clerkId,
        ActivityAction action,
        Integer actionId
) {}
