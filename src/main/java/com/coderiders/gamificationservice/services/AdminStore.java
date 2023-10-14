package com.coderiders.gamificationservice.services;

import com.coderiders.gamificationservice.models.Badge;
import com.coderiders.gamificationservice.models.db.Badges;
import com.coderiders.gamificationservice.models.db.PointsSystem;
import com.coderiders.gamificationservice.models.db.ReadingChallenges;
import com.coderiders.gamificationservice.models.enums.BadgeType;
import com.coderiders.gamificationservice.models.enums.ChallengeFrequency;

import java.util.List;
import java.util.Map;

public interface AdminStore {
    void initialize(List<Badges> badges, List<ReadingChallenges> challenges, List<PointsSystem> pointsSystems);
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
}
