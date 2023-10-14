package com.coderiders.gamificationservice.models.dto;

import com.coderiders.gamificationservice.models.enums.BadgeType;
import com.coderiders.gamificationservice.models.enums.ChallengeFrequency;

import java.time.LocalDateTime;

public record UserChallengesDTO (
        long id,
        String name,
        String description,
        ChallengeFrequency frequency,
        BadgeType type,
        int threshold,
        LocalDateTime challengeStartDate,
        LocalDateTime challengeEndDate,
        int pointsAwarded,
        LocalDateTime userChallengeStartDate
) {}
