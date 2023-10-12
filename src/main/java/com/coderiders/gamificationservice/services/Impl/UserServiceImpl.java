package com.coderiders.gamificationservice.services.Impl;

import com.coderiders.gamificationservice.models.UserStatistics;
import com.coderiders.gamificationservice.models.db.ReadingLogs;
import com.coderiders.gamificationservice.models.db.UserBadges;
import com.coderiders.gamificationservice.models.db.UserChallenges;
import com.coderiders.gamificationservice.models.requests.SavePages;
import com.coderiders.gamificationservice.repository.UserRepository;
import com.coderiders.gamificationservice.services.UserService;
import com.coderiders.gamificationservice.utilities.ReadingStreak;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.coderiders.gamificationservice.utilities.ReadingStreak.calculateReadingStreak;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public String updateUserPages(SavePages pages) {
        // Find highest tier user badges
        // get all user badges
        List<UserBadges> userBadges = userRepository.getUserBadges(pages.clerkId());
        System.out.println("userBadges");
        userBadges.forEach(System.out::println);

        // Add pages to ReadingLog
        userRepository.saveReadingLog(pages);

        // Total # of:
        // - Badges                 ----- IN UserStatistics
        // - Challenges             ----- IN UserStatistics
        // - Books                  ----- IN UserStatistics
        // - Pages Read             ----- IN UserStatistics
        // - Reviews Written?
        // - Reading Streak         ----- IN UserStatistics
        // - Friends                ----- IN UserStatistics
        UserStatistics stats = getUserStatistics(pages.clerkId());
        System.out.println("==================: " + stats);

        // Determine if user needs to be awarded any badges
        //




        //
        // If user wins any badges/challenges log to userPoints


        // Check if user is enrolled in a challenge

        List<UserChallenges> userChallenges = userRepository.getUserChallenges(pages.clerkId());
        System.out.println("userChallenges");
        userChallenges.forEach(System.out::println);
        //      - Have user object with each challenge with things like "progress" in it.

        return "SUCCESS";
    }

    @Override
    public List<ReadingLogs> getUserReadingLogs(String clerkId) {
        return userRepository.getUserReadingLogs(clerkId);
    }

    @Override
    public UserStatistics getUserStatistics(String clerkId) {
        UserStatistics oldStats = userRepository.getUserStatistics(clerkId);
        List<ReadingLogs> logs = getUserReadingLogs(clerkId);
        ReadingStreak.StreakResult result = calculateReadingStreak(logs);

        return new UserStatistics(oldStats.pagesRead(),
                oldStats.challengesCompleted(),
                oldStats.badgesEarned(),
                oldStats.booksRead(),
                oldStats.totalFriends(),
                result.length,
                result.justLost);
    }

}
