package com.coderiders.gamificationservice.models.db;

import com.coderiders.gamificationservice.models.enums.UserChallengeStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserChallenges {
    private long id;
    private String clerkId;
    private int challengeId;
    private LocalDateTime dateStarted;
    private LocalDateTime dateEnded;
    private UserChallengeStatus status;
}
