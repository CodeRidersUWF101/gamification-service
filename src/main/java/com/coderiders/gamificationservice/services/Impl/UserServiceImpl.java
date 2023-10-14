package com.coderiders.gamificationservice.services.Impl;

import com.coderiders.gamificationservice.models.Badge;
import com.coderiders.gamificationservice.models.UserStatistics;
import com.coderiders.gamificationservice.models.db.ReadingLogs;
import com.coderiders.gamificationservice.models.dto.UserChallengesDTO;
import com.coderiders.gamificationservice.models.enums.ActivityAction;
import com.coderiders.gamificationservice.models.enums.BadgeType;
import com.coderiders.gamificationservice.models.requests.SavePages;
import com.coderiders.gamificationservice.models.responses.Status;
import com.coderiders.gamificationservice.repository.UserRepository;
import com.coderiders.gamificationservice.services.AdminStore;
import com.coderiders.gamificationservice.services.UserService;
import com.coderiders.gamificationservice.utilities.ConsoleFormatter;
import com.coderiders.gamificationservice.utilities.Constants;
import com.coderiders.gamificationservice.utilities.ReadingStreak;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.coderiders.gamificationservice.utilities.ConsoleFormatter.printColored;
import static com.coderiders.gamificationservice.utilities.ReadingStreak.calculateReadingStreak;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AdminStore adminStore;

    @Override
    public Status updateUserPages(SavePages pages) {
        ActivityAction userAction = ActivityAction.getActivityActionByName(pages.action());
        List<ReadingLogs> logs = getUserReadingLogs(pages.clerkId());

        // get all user badges
        List<Badge> userBadges = userRepository.getUserBadges(pages.clerkId());

        // Add pages to ReadingLog
        userRepository.saveReadingLog(pages, userAction);

        // Get User Statistics
        UserStatistics stats = getUserStatistics(pages.clerkId(), logs);

        List<Badge> badgesEarned = determineUserBadges(userBadges, stats);

        // if badgesEarned has items
        if (!badgesEarned.isEmpty()) {
            userRepository.addBadgesToUser(pages.clerkId(), badgesEarned);
        }

        // Check if user is enrolled in a challenge
        List<UserChallengesDTO> userChallenges = userRepository.getUserChallenges(pages.clerkId());
        userChallenges.forEach(item -> printColored("UserChallenges: " + item, ConsoleFormatter.Color.GREEN));
        //      - Have user object with each challenge with things like "progress" in it.
        // What type of challenges will we have?
        //  - Read x pages per x
        //  - Read x books per x
        //  - Earn x badges in an x

        List<UserChallengesDTO> challengedCompleted = new ArrayList<>();

        return prepareReturnStatus(badgesEarned, challengedCompleted);
    }

    @Override
    public List<ReadingLogs> getUserReadingLogs(String clerkId) {
        return userRepository.getUserReadingLogs(clerkId);
    }

    private Status prepareReturnStatus(List<Badge> badgesEarned, List<UserChallengesDTO> challengedCompleted) {
        return new Status(Constants.SUCCESS, Constants.SUCCESS_DESCRIPTION, null, badgesEarned, challengedCompleted);
    }

    private UserStatistics getUserStatistics(String clerkId, List<ReadingLogs> logs) {
        UserStatistics oldStats = userRepository.getUserStatistics(clerkId);
        ReadingStreak.StreakResult result = calculateReadingStreak(logs);

        return new UserStatistics(oldStats.pagesRead(),
                oldStats.challengesCompleted(),
                oldStats.badgesEarned(),
                oldStats.booksRead(),
                oldStats.totalFriends(),
                oldStats.booksCollected(),
                result.length,
                result.justLost);
    }

    private List<Badge> determineUserBadges(List<Badge> userBadges, UserStatistics userStats) {
        List<Badge> badgesToAdd = new ArrayList<>();

        // Pages
        if (adminStore.isGreaterThanMin(BadgeType.PAGES, userStats.pagesRead())) {
            printColored("INSIDE PAGES!!!", ConsoleFormatter.Color.RED);
            Badge highestBadge = getHighestTierBadgeByType(userBadges, BadgeType.PAGES);
            badgesToAdd.addAll(getBadgesToAddToDB(highestBadge, userStats.pagesRead()));
        }

        // Friends
        if (adminStore.isGreaterThanMin(BadgeType.FRIENDS, userStats.totalFriends())) {
            Badge highestBadge = getHighestTierBadgeByType(userBadges, BadgeType.FRIENDS);
            badgesToAdd.addAll(getBadgesToAddToDB(highestBadge, userStats.totalFriends()));
        }

        // readingStreak
        if (adminStore.isGreaterThanMin(BadgeType.STREAK, userStats.readingStreak())) {
            Badge highestBadge = getHighestTierBadgeByType(userBadges, BadgeType.STREAK);
            badgesToAdd.addAll(getBadgesToAddToDB(highestBadge, userStats.readingStreak()));
        }

        // booksRead
        if (adminStore.isGreaterThanMin(BadgeType.BOOKS, userStats.booksRead())) {
            Badge highestBadge = getHighestTierBadgeByType(userBadges, BadgeType.BOOKS);
            badgesToAdd.addAll(getBadgesToAddToDB(highestBadge, userStats.booksRead()));
        }

        // booksCollected
        if (adminStore.isGreaterThanMin(BadgeType.COLLECTOR, userStats.booksCollected())) {
            Badge highestBadge = getHighestTierBadgeByType(userBadges, BadgeType.COLLECTOR);
            badgesToAdd.addAll(getBadgesToAddToDB(highestBadge, userStats.booksCollected()));
        }

        // badgesEarned
        if (adminStore.isGreaterThanMin(BadgeType.COMPLETION, userStats.badgesEarned())) {
            Badge highestBadge = getHighestTierBadgeByType(userBadges, BadgeType.COMPLETION);
            badgesToAdd.addAll(getBadgesToAddToDB(highestBadge, userStats.badgesEarned()));
        }

        // challengesCompleted
        if (adminStore.isGreaterThanMin(BadgeType.CHALLENGES, userStats.challengesCompleted())) {
            Badge highestBadge = getHighestTierBadgeByType(userBadges, BadgeType.CHALLENGES);
            badgesToAdd.addAll(getBadgesToAddToDB(highestBadge, userStats.challengesCompleted()));
        }

        return badgesToAdd;
    }


    private List<Badge> getBadgesToAddToDB(Badge currTier, int currentlyAt) {
        if (currTier == null) { return new ArrayList<>(); }

        int MAX_TIER = adminStore.getMaxTier(currTier.type());

        if (currTier.tier() >= MAX_TIER) return new ArrayList<>();

        List<Badge> badgesToAdd = new ArrayList<>();
        int currLevel = currTier.tier();

        int[] properThresholds = adminStore.getProperThresholds(currTier.type(), currLevel);

        int badgesEarned = 0;
        for (int threshold : properThresholds) {
            if (currentlyAt >= threshold) {
                badgesEarned++;
            } else {
                break;
            }
        }

        if ((currLevel + badgesEarned) > MAX_TIER) return new ArrayList<>();

        for(int i = 0; i < badgesEarned; i++) {
            short nextTier = (short) (currLevel + 1);
            badgesToAdd.add(adminStore.getNextBadge(currTier.type(), nextTier));
            currLevel += 1;
        }

        return badgesToAdd;
    }

    private Badge getHighestTierBadgeByType(List<Badge> userBadges, BadgeType badgeType) {
        return userBadges.stream()
                .filter(bad -> bad.type().getName().equalsIgnoreCase(badgeType.getName()))
                .max(Comparator.comparingInt(Badge::tier))
                .orElse(null);
    }



}