package com.coderiders.gamificationservice.models.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBadges {
    private long id;
    private String clerkId;
    private int BadgeId;
    private Timestamp dateEarned;
}
