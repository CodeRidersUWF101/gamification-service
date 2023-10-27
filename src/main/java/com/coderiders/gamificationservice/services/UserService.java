package com.coderiders.gamificationservice.services;

import com.coderiders.commonutils.models.*;
import com.coderiders.commonutils.models.records.UserBadge;
import com.coderiders.commonutils.models.requests.UpdateProgress;
import com.coderiders.gamificationservice.models.db.ReadingLogs;

import java.util.List;
import java.util.Map;

public interface UserService {
    Status updateUserPages(UpdateProgress pages);
    List<ReadingLogs> getUserReadingLogs(String clerkId);
    List<UserChallengesExtraDTO> getUserChallenges(String clerkId);
    Map<String, List<UserBadge>> getUserBadges(String clerkId);
    AddItem addItemToActivity(AddItem addItem);
    List<LatestAchievement> getLatestUserAchievements(String clerkId);
    SingleBookStats getSingleBookStats(String bookId, String clerkId);
    List<GamificationLeaderboard> getLeaderboard();
}
