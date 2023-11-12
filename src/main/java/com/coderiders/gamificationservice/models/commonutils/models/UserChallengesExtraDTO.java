package com.coderiders.gamificationservice.models.commonutils.models;

import com.coderiders.gamificationservice.models.commonutils.models.records.AdditionalChallengeInfo;
import com.coderiders.gamificationservice.models.commonutils.models.records.DateProgress;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;


@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserChallengesExtraDTO {
    private Long id;
    private Long userChallengeId;
    private String name;
    private String description;
    private String frequency;
    private String type;
    private Integer threshold;
    private Integer duration;
    private String challengeStartDate;
    private String challengeEndDate;
    private Integer pointsAwarded;
    private String userChallengeStartDate;
    private String userChallengeEndDate;
    private String status;


    private DateProgress dateProgress;
    private AdditionalChallengeInfo additionalInfo;
}

