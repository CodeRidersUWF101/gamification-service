package com.coderiders.gamificationservice.utilities;

import com.coderiders.commonutils.models.ReadingChallenges;
import com.coderiders.commonutils.models.UserChallengesExtraDTO;
import com.coderiders.commonutils.models.enums.ActivityAction;
import com.coderiders.commonutils.models.records.Badge;
import com.coderiders.commonutils.models.records.UserChallengesDTO;
import com.coderiders.gamificationservice.models.dto.UserActivityDTO;
import com.coderiders.gamificationservice.models.dto.UserPointsDTO;
import com.coderiders.gamificationservice.models.enums.ElementType;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {
    public static LocalDateTime convertToLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    public static UserActivityDTO badgeToActivityDTO(String clerkId, ActivityAction action, int id) {
        return new UserActivityDTO(clerkId, action, id);
    }

    public static UserPointsDTO badgeToPointsDTO(String clerkId, Badge badge) {
        return new UserPointsDTO(clerkId, badge.getPointsAwarded(), ElementType.BADGE, badge.getTier(), Math.toIntExact(badge.getId()));
    }

    public static String toCustomFormat(LocalDateTime dateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    public static String toSimpleDateFormat(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public static String toReadableFormat(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
    }

    public static String toReadableFormat(LocalDate dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    public static UserChallengesExtraDTO readingChallengeToDTO(ReadingChallenges challenges) {
        return UserChallengesExtraDTO
                .builder()
                .id(challenges.getId())
                .userChallengeId(null)
                .name(challenges.getName())
                .description(challenges.getDescription())
                .frequency(challenges.getFrequency().getName())
                .type(challenges.getType().getName())
                .threshold(challenges.getThreshold())
                .duration(challenges.getDuration())
                .challengeStartDate(String.valueOf(challenges.getStartDate()))
                .challengeEndDate(String.valueOf(challenges.getEndDate()))
                .pointsAwarded(challenges.getPointsAwarded())
                .build();
    }

    public static UserChallengesExtraDTO userChallengesToDTO(UserChallengesDTO challenges) {
        return UserChallengesExtraDTO
                .builder()
                .id(challenges.getId())
                .userChallengeId(challenges.getUserChallengeId())
                .name(challenges.getName())
                .description(challenges.getDescription())
                .frequency(challenges.getFrequency().getName())
                .type(challenges.getType().getName())
                .threshold(challenges.getThreshold())
                .duration(challenges.getDuration())
                .challengeStartDate(String.valueOf(challenges.getChallengeStartDate()))
                .challengeEndDate(String.valueOf(challenges.getChallengeEndDate()))
                .pointsAwarded(challenges.getPointsAwarded())
                .userChallengeStartDate(String.valueOf(challenges.getUserChallengeStartDate()))
                .status(challenges.getStatus().getName())
                .build();
    }
}
