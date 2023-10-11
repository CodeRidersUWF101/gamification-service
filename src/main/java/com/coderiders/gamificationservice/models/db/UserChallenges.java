package com.coderiders.gamificationservice.models.db;

import com.coderiders.gamificationservice.models.enums.UserChallengeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserChallenges {
    private long id;
    private String clerkId;
    private int ChallengeId;
    private Timestamp dateStarted;
    private Timestamp dateEnded;
    private UserChallengeStatus status;
}
