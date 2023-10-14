package com.coderiders.gamificationservice.services;

import com.coderiders.gamificationservice.models.db.ReadingLogs;
import com.coderiders.gamificationservice.models.requests.SavePages;
import com.coderiders.gamificationservice.models.responses.Status;

import java.util.List;

public interface UserService {
    Status updateUserPages(SavePages pages);
    List<ReadingLogs> getUserReadingLogs(String clerkId);
}
