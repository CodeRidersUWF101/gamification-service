package com.coderiders.gamificationservice.models.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Leaderboard {
    private long id;
    private String clerkId;
    private boolean isVisible;
    private int points;
    private int rank;
}
