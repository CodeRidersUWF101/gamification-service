package com.coderiders.gamificationservice.services;

import com.coderiders.gamificationservice.models.db.Badges;
import com.coderiders.gamificationservice.models.db.ReadingChallenges;
import com.coderiders.gamificationservice.models.enums.BadgeType;
import com.coderiders.gamificationservice.models.enums.ChallengeFrequency;

import java.util.List;


@Deprecated
public interface AdminService {

    List<Badges> getAllBadges();
    List<Badges> getAllBadgesByType(BadgeType type);
    List<Badges> getAllBadgesByTier(short tier);
    Badges getBadgeById(int id);
    List<ReadingChallenges> getAllChallenges();
    List<ReadingChallenges> getAllChallengesByTime(boolean isLimitedTime);
    List<ReadingChallenges> getAllChallengesByType(ChallengeFrequency challenges);
}
