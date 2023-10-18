package com.coderiders.gamificationservice.services;

import com.coderiders.commonutils.models.records.UserBadge;
import com.coderiders.gamificationservice.models.db.ReadingLogs;
import com.coderiders.gamificationservice.models.requests.SavePages;
import com.coderiders.gamificationservice.models.responses.Status;
import com.coderiders.gamificationservice.models.responses.UserChallengesExtraDTO;

import java.util.List;
import java.util.Map;

public interface UserService {
    Status updateUserPages(SavePages pages);
    List<ReadingLogs> getUserReadingLogs(String clerkId);
    List<UserChallengesExtraDTO> getUserChallenges(String clerkId);
    Map<String, List<UserBadge>> getUserBadges(String clerkId);
}
