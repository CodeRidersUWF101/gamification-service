package com.coderiders.gamificationservice.utilities;

import com.coderiders.gamificationservice.models.Badge;
import com.coderiders.gamificationservice.models.dto.UserActivityDTO;
import com.coderiders.gamificationservice.models.dto.UserPointsDTO;
import com.coderiders.gamificationservice.models.enums.ActivityAction;
import com.coderiders.gamificationservice.models.enums.ElementType;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Utils {
    public static LocalDateTime convertToLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    public static UserActivityDTO badgeToActivityDTO(String clerkId, ActivityAction action, int id) {
        return new UserActivityDTO(clerkId, action, id);
    }

    public static UserPointsDTO badgeToPointsDTO(String clerkId, Badge badge) {
        return new UserPointsDTO(clerkId, badge.pointsAwarded(), ElementType.BADGE, badge.tier(), (int) badge.id());
    }

}
