package com.coderiders.gamificationservice.services.Impl;

import com.coderiders.gamificationservice.models.Badge;
import com.coderiders.gamificationservice.models.UserStatistics;
import com.coderiders.gamificationservice.models.db.ReadingLogs;
import com.coderiders.gamificationservice.models.db.UserChallenges;
import com.coderiders.gamificationservice.models.enums.ActivityAction;
import com.coderiders.gamificationservice.models.enums.BadgeThresholds;
import com.coderiders.gamificationservice.models.enums.BadgeType;
import com.coderiders.gamificationservice.models.requests.SavePages;
import com.coderiders.gamificationservice.repository.UserRepository;
import com.coderiders.gamificationservice.services.AdminStore;
import com.coderiders.gamificationservice.services.UserService;
import com.coderiders.gamificationservice.utilities.Constants;
import com.coderiders.gamificationservice.utilities.ReadingStreak;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.coderiders.gamificationservice.utilities.ReadingStreak.calculateReadingStreak;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AdminStore adminStore;

    @Override
    public String updateUserPages(SavePages pages) {
        ActivityAction userAction = ActivityAction.getActivityActionByName(pages.action());
        List<ReadingLogs> logs = getUserReadingLogs(pages.clerkId());

        // get all user badges
        List<Badge> userBadges = userRepository.getUserBadges(pages.clerkId());

        // Add pages to ReadingLog
        userRepository.saveReadingLog(pages);

        // Save User Action // TODO: Turn this into DB Trigger // DONE
//        userRepository.saveSingleUserActivity(pages.clerkId(), userAction, null);

        // Get User Statistics
        UserStatistics stats = getUserStatistics(pages.clerkId(), logs);

        // If user earns any badges log to userPoints
        List<Badge> badgesEarned = determineUserBadges(userBadges, stats);

        // if badgesEarned has items
        if (!badgesEarned.isEmpty()) {
            userRepository.addBadgesToUser(pages.clerkId(), badgesEarned);
            // Save entries to userPoints and userNotifications.

            // TODO: DB TRIGGER? // DONE
//            List<UserActivityDTO> activityDTOS = badgesEarned.stream()
//                    .map(item -> Utils.badgeToActivityDTO(pages.clerkId(), ActivityAction.EARNED_BADGE, (int) item.id())).toList();
//            userRepository.saveManyUserActivity(activityDTOS);

            // save to userPoints
//            List<UserPointsDTO> pointsDTOS = badgesEarned.stream()
//                    .map(item -> Utils.badgeToPointsDTO(pages.clerkId(), item)).toList();
//            userRepository.saveManyUserPoints(pointsDTOS);

            // save to userNotifications


        } else {
            // Badges to save is empty
        }

        // Check if user is enrolled in a challenge
        List<UserChallenges> userChallenges = userRepository.getUserChallenges(pages.clerkId());
        userChallenges.forEach(System.out::println);
        //      - Have user object with each challenge with things like "progress" in it.
        // What type of challenges will we have?
        //  - Read x pages per x
        //  - Read x books per x
        //  - Earn x badges in an x
        // -

        return "SUCCESS";
    }

    @Override
    public List<ReadingLogs> getUserReadingLogs(String clerkId) {
        return userRepository.getUserReadingLogs(clerkId);
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

        // if pages > 200
        if (BadgeThresholds.PAGES.isGreaterThanMin(userStats.pagesRead())) {
            Badge highestBadge = getHighestTierBadgeByType(userBadges, BadgeType.PAGES);
            badgesToAdd.addAll(getBadgesToAddToDB(highestBadge, userStats.pagesRead(), Constants.pageThresholds));
        }

        // if friend > 1
        if (BadgeThresholds.FRIENDS.isGreaterThanMin(userStats.totalFriends())) {
            Badge highestBadge = getHighestTierBadgeByType(userBadges, BadgeType.FRIENDS);
            badgesToAdd.addAll(getBadgesToAddToDB(highestBadge, userStats.totalFriends(), Constants.friendsThresholds));
        }

        // if readingStreak > 7
        if (userStats.readingStreak() > Constants.STREAK_MIN) {
            Badge highestBadge = getHighestTierBadgeByType(userBadges, BadgeType.STREAK);
            badgesToAdd.addAll(getBadgesToAddToDB(highestBadge, userStats.readingStreak(), Constants.streakThresholds));
        }

        // if booksRead > 5
        if (userStats.booksRead() > Constants.BOOKS_READ_MIN) {
            Badge highestBadge = getHighestTierBadgeByType(userBadges, BadgeType.BOOKS);
            badgesToAdd.addAll(getBadgesToAddToDB(highestBadge, userStats.booksRead(), Constants.booksReadThresholds));
        }

        // if booksCollected > 10
        if (userStats.booksCollected() > Constants.BOOKS_COLLECTED_MIN) {
            Badge highestBadge = getHighestTierBadgeByType(userBadges, BadgeType.COLLECTOR);
            badgesToAdd.addAll(getBadgesToAddToDB(highestBadge, userStats.booksCollected(), Constants.booksCollectedThresholds));
        }

        // if badgesEarned > 1
        if (userStats.badgesEarned() > Constants.BADGES_MIN) {
            Badge highestBadge = getHighestTierBadgeByType(userBadges, BadgeType.COMPLETION);
            badgesToAdd.addAll(getBadgesToAddToDB(highestBadge, userStats.badgesEarned(), Constants.badgesThresholds));
        }

        // if challengesCompleted > 1
        if (userStats.challengesCompleted() > Constants.CHALLENGES_MIN) {
            Badge highestBadge = getHighestTierBadgeByType(userBadges, BadgeType.CHALLENGES);
            badgesToAdd.addAll(getBadgesToAddToDB(highestBadge, userStats.challengesCompleted(), Constants.challengeThresholds));
        }

        return badgesToAdd;
        }


    private List<Badge> getBadgesToAddToDB(Badge currTier, int currentlyAt, final int[] thresholds) {
        if (currTier == null) { return new ArrayList<>(); }
        if (currTier.tier() >= Constants.MAX_TIER) return new ArrayList<>();

        List<Badge> badgesToAdd = new ArrayList<>();
        int currLevel = currTier.tier();

        int[] properThresholds = Arrays.stream(thresholds, currLevel, thresholds.length).toArray();

        int badgesEarned = 0;

        for (int threshold : properThresholds) {
            if (currentlyAt >= threshold) {
                badgesEarned++;
            } else {
                break;
            }
        }

        if ((currLevel + badgesEarned) > Constants.MAX_TIER) return new ArrayList<>();

        for(int i = 0; i < badgesEarned; i++) {
            short nextTier = (short) (currLevel + 1); // FIXME: Static Casting =(
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

/*
    // if pages > 200
        if (BadgeThresholds.PAGES.isBetweenThreshold(userStatistics.pagesRead())) {
        System.out.println("\n\n=============@@@@@@@@@@@================ PAGES IF STATEMENT RAN\n\n");
        Badge highestBadge = getHighestTierBadgeByType(userBadges, BadgeType.PAGES);
        badgesToAdd.addAll(getBadgesToAddToDB(highestBadge, userStatistics, Constants.pageThresholds));
    }

    // if friend > 1
        if (BadgeThresholds.FRIENDS.isBetweenThreshold(userStatistics.totalFriends())) {
        Badge highestBadge = getHighestTierBadgeByType(userBadges, BadgeType.FRIENDS);
        badgesToAdd.addAll(getBadgesToAddToDB(highestBadge, userStatistics, Constants.friendsThresholds));
    }

    // if readingStreak > 7
        if (BadgeThresholds.STREAK.isBetweenThreshold(userStatistics.readingStreak())) {
        Badge highestBadge = getHighestTierBadgeByType(userBadges, BadgeType.STREAK);
        badgesToAdd.addAll(getBadgesToAddToDB(highestBadge, userStatistics, Constants.streakThresholds));
    }

    // booksRead
        if (BadgeThresholds.BOOKS_READ.isBetweenThreshold(userStatistics.booksRead())) {
        Badge highestBadge = getHighestTierBadgeByType(userBadges, BadgeType.BOOKS);
        badgesToAdd.addAll(getBadgesToAddToDB(highestBadge, userStatistics, Constants.booksReadThresholds));
    }

    // if booksCollected > 10
        if (BadgeThresholds.BOOKS_COLLECTED.isBetweenThreshold(userStatistics.booksCollected())) {
        Badge highestBadge = getHighestTierBadgeByType(userBadges, BadgeType.COLLECTOR);
        badgesToAdd.addAll(getBadgesToAddToDB(highestBadge, userStatistics, Constants.booksCollectedThresholds));
    }

    // if badgesEarned > 1
        if (BadgeThresholds.BADGES.isBetweenThreshold(userStatistics.badgesEarned())) {
        Badge highestBadge = getHighestTierBadgeByType(userBadges, BadgeType.COMPLETION);
        badgesToAdd.addAll(getBadgesToAddToDB(highestBadge, userStatistics, Constants.badgesThresholds));

    }

    // if challengesCompleted > 1
        if (BadgeThresholds.CHALLENGES.isBetweenThreshold(userStatistics.challengesCompleted())) {
        Badge highestBadge = getHighestTierBadgeByType(userBadges, BadgeType.CHALLENGES);
        badgesToAdd.addAll(getBadgesToAddToDB(highestBadge, userStatistics, Constants.challengeThresholds));
    }
*/