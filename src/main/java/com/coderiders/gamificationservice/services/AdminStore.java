package com.coderiders.gamificationservice.services;

import com.coderiders.gamificationservice.models.commonutils.models.ReadingChallenges;
import com.coderiders.gamificationservice.models.commonutils.models.enums.BadgeType;
import com.coderiders.gamificationservice.models.commonutils.models.enums.ChallengeFrequency;
import com.coderiders.gamificationservice.models.commonutils.models.records.Badge;
import com.coderiders.gamificationservice.models.db.Badges;
import com.coderiders.gamificationservice.models.db.PointsSystem;
import com.coderiders.gamificationservice.models.dto.TiersThresholdsDTO;

import java.util.List;
import java.util.Map;

public interface AdminStore {
    void initialize(List<Badges> badges, List<ReadingChallenges> challenges, List<PointsSystem> pointsSystems, List<TiersThresholdsDTO> thresholdsDTOS);

    Map<String, Map<Short, Badge>> getAllBadgesByType();
    List<Badge> getAllBadgesList();
    Badge getBadgeByTypeAndTier(BadgeType type, short tier);
    Badge getNextBadge(BadgeType type, short tier);
    Badge getBadgeById(long id);

    ReadingChallenges getChallengeById(long id);
    List<ReadingChallenges> getPermanentChallenges();
    List<ReadingChallenges> getTemporaryChallenges();
    List<ReadingChallenges> getAllChallengesByType(ChallengeFrequency type);
    List<ReadingChallenges> getAllChallenges();

    Map<String, TiersThresholdsDTO> getTiersAndThresholds();
    boolean isGreaterThanMin(BadgeType type, int value);
    int[] getProperThresholds(BadgeType type, int currLevel);
    int[] getThresholds(BadgeType type);
    int getMaxTier(BadgeType type);
}
