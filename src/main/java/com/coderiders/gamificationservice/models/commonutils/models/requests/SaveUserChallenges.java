package com.coderiders.gamificationservice.models.commonutils.models.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveUserChallenges {
    private String clerkId;
    private int challengeId;

}