package com.coderiders.gamificationservice.models.db;

import com.coderiders.gamificationservice.models.enums.ChallengeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingChallenges {

    private long id;
    private String name;
    private String description;
    private ChallengeType type;
    private Timestamp startDate;
    private Timestamp endDate;
    private int pointsAwarded;
}
