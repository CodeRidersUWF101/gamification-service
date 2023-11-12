package com.coderiders.gamificationservice.models.commonutils.models;

import com.coderiders.gamificationservice.models.commonutils.models.records.BadgeWithNext;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Status {
    private String statusCode;
    private String statusDescription;
    private List<AdditionalStatus> additionalStatuses;
    private List<BadgeWithNext> badgesEarned;
    List<UserChallengesExtraDTO> challenges;
}
