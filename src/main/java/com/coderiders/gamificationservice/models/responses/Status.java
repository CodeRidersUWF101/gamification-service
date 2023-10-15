package com.coderiders.gamificationservice.models.responses;


import java.util.List;


public record Status (
        String statusCode,
        String statusDescription,
        List<AdditionalStatus> additionalStatuses,
        List<BadgeWithNext> badgesEarned,
        List<UserChallengesExtraDTO> challenges
) {}
