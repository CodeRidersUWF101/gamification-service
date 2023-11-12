package com.coderiders.gamificationservice.models.commonutils.models.records;


import com.coderiders.gamificationservice.models.commonutils.models.enums.ActivityAction;
import com.coderiders.gamificationservice.models.commonutils.models.enums.BadgeType;
import com.coderiders.gamificationservice.models.commonutils.models.enums.ChallengeFrequency;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserChallengesDTO {
    private Long id;
    private Long userChallengeId;
    private String name;
    private String description;
    private ChallengeFrequency frequency;
    private BadgeType type;
    private Integer threshold;
    private Integer duration;
    private LocalDateTime challengeStartDate;
    private LocalDateTime challengeEndDate;
    private Integer pointsAwarded;
    private LocalDateTime userChallengeStartDate;
    private ActivityAction status;
}