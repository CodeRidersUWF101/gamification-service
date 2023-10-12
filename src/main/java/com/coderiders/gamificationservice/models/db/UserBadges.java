package com.coderiders.gamificationservice.models.db;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserBadges {
    private long id;
    private String clerkId;
    private int BadgeId;
    private LocalDateTime dateEarned;
}
