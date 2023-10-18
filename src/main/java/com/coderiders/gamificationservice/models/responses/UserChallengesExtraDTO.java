package com.coderiders.gamificationservice.models.responses;

import com.coderiders.gamificationservice.models.db.ReadingChallenges;
import com.coderiders.gamificationservice.models.dto.UserChallengesDTO;
import com.coderiders.gamificationservice.models.enums.ActivityAction;
import com.coderiders.gamificationservice.models.enums.BadgeType;
import com.coderiders.gamificationservice.models.enums.ChallengeFrequency;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserChallengesExtraDTO {
    private long id;
    private long userChallengeId;
    private String name;
    private String description;
    private ChallengeFrequency frequency;
    private BadgeType type;
    private int threshold;
    private int duration;
    private LocalDateTime challengeStartDate;
    private LocalDateTime challengeEndDate;
    private int pointsAwarded;
    private LocalDateTime userChallengeStartDate;
    private LocalDateTime userChallengeEndDate;
    private ActivityAction status;


    private DateProgress dateProgress;
    private AdditionalChallengeInfo additionalInfo;


    public static UserChallengesExtraDTO userChallengesToDTO(UserChallengesDTO dto) {
        return UserChallengesExtraDTO.builder()
                .id(dto.id())
                .userChallengeId(dto.userChallengeId())
                .name(dto.name())
                .description(dto.description())
                .frequency(dto.frequency())
                .type(dto.type())
                .threshold(dto.threshold())
                .duration(dto.duration())
                .challengeEndDate(dto.challengeEndDate())
                .challengeStartDate(dto.challengeStartDate())
                .pointsAwarded(dto.pointsAwarded())
                .userChallengeStartDate(dto.userChallengeStartDate())
                .status(dto.status())
                .build();
    }

    public static UserChallengesExtraDTO readingChallengeToDTO(ReadingChallenges challenges) {
        return UserChallengesExtraDTO.builder()
                .id(challenges.getId())
                .name(challenges.getName())
                .description(challenges.getDescription())
                .frequency(challenges.getFrequency())
                .type(challenges.getType())
                .duration(challenges.getDuration())
                .threshold(challenges.getThreshold())
                .challengeEndDate(challenges.getEndDate())
                .challengeStartDate(challenges.getStartDate())
                .pointsAwarded(challenges.getPointsAwarded())
                .build();

    }

}
