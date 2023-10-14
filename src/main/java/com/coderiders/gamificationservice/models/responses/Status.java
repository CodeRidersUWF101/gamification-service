package com.coderiders.gamificationservice.models.responses;

import com.coderiders.gamificationservice.models.Badge;
import com.coderiders.gamificationservice.models.dto.UserChallengesDTO;

import java.util.List;


public record Status (
        String statusCode,
        String statusDescription,
        List<AdditionalStatus> additionalStatuses,
        List<Badge> badgesEarned,
        List<UserChallengesDTO> challengesCompleted
) {}
