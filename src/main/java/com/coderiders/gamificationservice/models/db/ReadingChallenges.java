package com.coderiders.gamificationservice.models.db;

import com.coderiders.gamificationservice.models.enums.BadgeType;
import com.coderiders.gamificationservice.models.enums.ChallengeFrequency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingChallenges {

    private long id;
    private String name;
    private String description;
    private ChallengeFrequency frequency;
    private BadgeType type;
    private int threshold;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int pointsAwarded;
}
