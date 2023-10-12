package com.coderiders.gamificationservice.services;

import com.coderiders.gamificationservice.models.UserStatistics;
import com.coderiders.gamificationservice.models.db.ReadingLogs;
import com.coderiders.gamificationservice.models.requests.SavePages;

import java.util.List;

public interface UserService {
    String updateUserPages(SavePages pages);
    List<ReadingLogs> getUserReadingLogs(String clerkId);
    UserStatistics getUserStatistics(String clerkId);
}
