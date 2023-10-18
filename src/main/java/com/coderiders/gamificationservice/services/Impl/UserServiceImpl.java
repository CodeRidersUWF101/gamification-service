package com.coderiders.gamificationservice.services.Impl;

import com.coderiders.commonutils.models.records.UserBadge;
import com.coderiders.gamificationservice.models.Badge;
import com.coderiders.gamificationservice.models.UserStatistics;
import com.coderiders.gamificationservice.models.db.ReadingLogs;
import com.coderiders.gamificationservice.models.dto.UserChallengesDTO;
import com.coderiders.gamificationservice.models.enums.ActivityAction;
import com.coderiders.gamificationservice.models.enums.BadgeType;
import com.coderiders.gamificationservice.models.requests.SavePages;
import com.coderiders.gamificationservice.models.responses.*;
import com.coderiders.gamificationservice.repository.UserRepository;
import com.coderiders.gamificationservice.services.AdminStore;
import com.coderiders.gamificationservice.services.UserService;
import com.coderiders.gamificationservice.utilities.ConsoleFormatter;
import com.coderiders.gamificationservice.utilities.Constants;
import com.coderiders.gamificationservice.utilities.ReadingStreak;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.coderiders.gamificationservice.utilities.ConsoleFormatter.printColored;
import static com.coderiders.gamificationservice.utilities.ReadingStreak.calculateReadingStreak;
import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AdminStore adminStore;
    private final List<String> validChallengeSaveActions = List.of(
            ActivityAction.COMPLETED_CHALLENGE.getName(),
            ActivityAction.FAILED_CHALLENGE.getName());

    @Override
    public Status updateUserPages(SavePages pages) {
        ActivityAction userAction = ActivityAction.getActivityActionByName(pages.action());

        // get all user badges
        List<UserBadge> userBadges = userRepository.getUserBadges(pages.clerkId());

        // Add pages to ReadingLog
        userRepository.saveReadingLog(pages, userAction);
        List<ReadingLogs> logs = getUserReadingLogs(pages.clerkId());

        // Get User Statistics
        UserStatistics stats = getUserStatistics(pages.clerkId(), logs);

        List<Badge> badgesEarned = determineUserBadges(userBadges, stats);

        // if badgesEarned has items
        if (!badgesEarned.isEmpty()) {
            userRepository.addBadgesToUser(pages.clerkId(), badgesEarned);
        }

        // Check if user is enrolled in a challenge
        List<UserChallengesDTO> userChallenges = userRepository.getUserChallenges(pages.clerkId());

        List<UserChallengesExtraDTO> updatedChallenges = determineUserChallenges(userChallenges, logs);

        List<UserChallengesExtraDTO> challengesToUpdate = updatedChallenges.stream()
                .filter(item -> validChallengeSaveActions.contains(item.getStatus().getName()))
                .toList();

        userRepository.updateUserChallenges(challengesToUpdate, pages.clerkId());

        return prepareReturnStatus(badgesEarned, updatedChallenges);
    }

    @Override
    public List<ReadingLogs> getUserReadingLogs(String clerkId) {
        return userRepository.getUserReadingLogs(clerkId);
    }

    @Override
    public List<UserChallengesExtraDTO> getUserChallenges(String clerkId) {
        List<ReadingLogs> logs = getUserReadingLogs(clerkId);
        List<UserChallengesDTO> userChallenges = userRepository.getUserChallenges(clerkId);

        Map<Long, UserChallengesExtraDTO> updatedChallengesMap = determineUserChallenges(userChallenges, logs).stream()
                .filter(item -> ActivityAction.STARTED_CHALLENGE.getName().equalsIgnoreCase(item.getStatus().getName()))
                .collect(Collectors.toMap(UserChallengesExtraDTO::getId, Function.identity()));

        Set<Long> challengeIds = updatedChallengesMap.keySet();

        return adminStore.getAllChallenges().stream()
                .map(UserChallengesExtraDTO::readingChallengeToDTO)
                .map(chall -> challengeIds.contains(chall.getId()) ? updatedChallengesMap.get(chall.getId()) : chall)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<UserBadge>> getUserBadges(String clerkId) {
        List<UserBadge> userBadges = userRepository.getUserBadges(clerkId);
        List<Long> userBadgeIds = userBadges.stream().map(UserBadge::id).toList();

        // TODO: Add a You have x many x until you earn the next badge! To this return.
        //        List<ReadingLogs> logs = getUserReadingLogs(clerkId);
        //        // Get User Statistics
        //        UserStatistics stats = getUserStatistics(clerkId, logs);

        return adminStore.getAllBadgesList().stream()
                .map(this::badgeToUserBadge)
                .map(item -> userBadgeIds.contains(item.id())
                        ? userBadges.stream().filter(b -> b.id() == item.id()).findFirst().get()
                        : item)
                .collect(Collectors.groupingBy(badge -> badge.type().getName()));
    }

    private UserBadge badgeToUserBadge(Badge badge) {
        return new UserBadge(
                badge.id(),
                badge.name(),
                badge.description(),
                badge.threshold(),
                com.coderiders.commonutils.models.enums.BadgeType.getBadgeTypeByName(badge.type().getName()),
                badge.tier(),
                badge.imageUrl(),
                badge.pointsAwarded(),
                null);
    }

    private Status prepareReturnStatus(List<Badge> badgesEarned, List<UserChallengesExtraDTO> challengedCompleted) {
        List<BadgeWithNext> returnBadges = badgesEarned.stream().map(this::badgeToWithNext).toList();

        return new Status(Constants.SUCCESS, Constants.SUCCESS_DESCRIPTION, null, returnBadges, challengedCompleted);
    }

    // TODO: Find a better way to do this.
    private List<UserChallengesExtraDTO> determineUserChallenges(List<UserChallengesDTO> userChallenges, List<ReadingLogs> logs) {
        List<UserChallengesExtraDTO> returnChallenges = new ArrayList<>();

        for (UserChallengesDTO challenge : userChallenges) {

            // if startDate or EndDate is null, it's a permanent event and needs to be determined by user.
            if (challenge.challengeStartDate() == null || challenge.challengeEndDate() == null) {
                LocalDate userChallengeStartDate = challenge.userChallengeStartDate().toLocalDate();
                LocalDate userEndDate = userChallengeStartDate.plusDays(challenge.duration() - 1);

                Predicate<ReadingLogs> filteredLogsToProperRange = log ->
                        (log.getDate().isAfter(userChallengeStartDate.atStartOfDay())
                                || log.getDate().isEqual(userChallengeStartDate.atStartOfDay()))
                        && (log.getDate().isBefore(userEndDate.plusDays(1).atStartOfDay())
                                || log.getDate().isEqual(userEndDate.atStartOfDay()));

                Stream<ReadingLogs> filteredLogs = logs.stream().filter(filteredLogsToProperRange);

                UserChallengesExtraDTO updatedChallenge = switch (challenge.type()) {
                    case STREAK -> checkStreak(filteredLogs, challenge);
                    case PAGES -> checkPages(filteredLogs, challenge);
                    case BOOKS -> checkBooksRead(filteredLogs, challenge);
                    default -> null;
                };

                if (updatedChallenge != null) {
                    updatedChallenge.setUserChallengeEndDate(userEndDate.atTime(23, 59, 59));
                    returnChallenges.add(updatedChallenge);
                }

            } else { // timed challenge - handled differently
                printColored("TIMED CHALLENGE", ConsoleFormatter.Color.RED);
            }

        }
        return returnChallenges;
    }

    private UserChallengesExtraDTO checkBooksRead(Stream<ReadingLogs> logs, UserChallengesDTO challengesDTO) {

        int totalNumberOfBooksRead = (int) logs
                .filter(item -> item.getAction().equals(ActivityAction.COMPLETED_BOOK))
                .count();

        return checkChallengeByThreshold(challengesDTO, totalNumberOfBooksRead);
    }

    private UserChallengesExtraDTO checkPages(Stream<ReadingLogs> logs, UserChallengesDTO challengesDTO) {

        int totalNumberOfPagesRead = logs
                .mapToInt(ReadingLogs::getPagesRead)
                .sum();

        return checkChallengeByThreshold(challengesDTO, totalNumberOfPagesRead);
    }

    private UserChallengesExtraDTO checkChallengeByThreshold(UserChallengesDTO challenge, int userActual) {
        UserChallengesExtraDTO updatedChallenge = UserChallengesExtraDTO.userChallengesToDTO(challenge);
        int challengeThreshold = challenge.threshold();

        LocalDate today = LocalDate.now();
        LocalDate userChallengeStartDate = challenge.userChallengeStartDate().toLocalDate();
        LocalDate userEndDate = userChallengeStartDate.plusDays(challenge.duration() - 1);

        if (today.isBefore(userEndDate) || today.isEqual(userEndDate)) {

            long totalDays = DAYS.between(userChallengeStartDate, userEndDate.plusDays(1));
            int daysCompleted = (int) DAYS.between(userChallengeStartDate.minusDays(1), today);
            int remainingDays = (int) DAYS.between(today, userEndDate);
            double percentageComplete = ((double) daysCompleted / totalDays) * 100;

            // User has completed the challenge.
            if (userActual >= challengeThreshold) {

                updatedChallenge.setStatus(ActivityAction.COMPLETED_CHALLENGE);
                DateProgress dateProgress = new DateProgress(percentageComplete, daysCompleted, remainingDays);
                updatedChallenge.setDateProgress(dateProgress);

            } else { // Challenge Ongoing

                double percentageOfChallengeComplete = ((double) userActual / challengeThreshold) * 100;
                double pagesToGo = challengeThreshold - userActual;

                AdditionalChallengeInfo additionalInfo = new AdditionalChallengeInfo(
                        userActual,
                        pagesToGo,
                        percentageOfChallengeComplete);

                updatedChallenge.setAdditionalInfo(additionalInfo);
                updatedChallenge.setStatus(ActivityAction.STARTED_CHALLENGE);
                DateProgress dateProgress = new DateProgress(percentageComplete, daysCompleted, remainingDays);
                updatedChallenge.setDateProgress(dateProgress);
            }
            return updatedChallenge;
        }

        // User has failed the challenge.
        updatedChallenge.setStatus(ActivityAction.FAILED_CHALLENGE);
        return updatedChallenge;
    }


    private UserChallengesExtraDTO checkStreak(Stream<ReadingLogs> filteredLogs, UserChallengesDTO challenge) {

        LocalDate today = LocalDate.now();
        LocalDate userChallengeStartDate = challenge.userChallengeStartDate().toLocalDate();
        LocalDate userEndDate = userChallengeStartDate.plusDays(challenge.duration() - 1);

        UserChallengesExtraDTO updatedChallenge = UserChallengesExtraDTO.userChallengesToDTO(challenge);

        // List of expected Days
        List<LocalDate> allExpectedDates = getExpectedDays(userChallengeStartDate);

        // List of actual days
        List<LocalDate> filteredLogDateTime = filteredLogs
                .map(item -> item.getDate().toLocalDate())
                .sorted()
                .toList();

        boolean hasLogForEachDay = allExpectedDates.stream()
                .allMatch(expectedDate -> filteredLogDateTime.stream()
                        .anyMatch(loggedDate -> loggedDate.isEqual(expectedDate)));

        long totalDays = DAYS.between(userChallengeStartDate, userEndDate.plusDays(1));
        int daysCompleted = (int) DAYS.between(userChallengeStartDate.minusDays(1), today);
        int remainingDays = (int) DAYS.between(today, userEndDate);
        double percentageComplete = ((double) daysCompleted / totalDays) * 100;

        if (hasLogForEachDay) {  // At least one log for exists for each day exists
            if (today.isAfter(userEndDate) || today.isEqual(userEndDate)) { // User has completed the challenge.
                updatedChallenge.setStatus(ActivityAction.COMPLETED_CHALLENGE);

            } else { // User has not failed but is not over yet.
                AdditionalChallengeInfo additionalInfo = new AdditionalChallengeInfo(
                        daysCompleted,
                        remainingDays,
                        percentageComplete);

                updatedChallenge.setAdditionalInfo(additionalInfo);
            }

            DateProgress dateProgress = new DateProgress(percentageComplete, daysCompleted, remainingDays);
            updatedChallenge.setDateProgress(dateProgress);

            return updatedChallenge;
        }

        updatedChallenge.setStatus(ActivityAction.FAILED_CHALLENGE);
        return updatedChallenge;
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

    private List<Badge> determineUserBadges(List<UserBadge> userBadges, UserStatistics userStats) {
        List<Badge> badgesToAdd = new ArrayList<>();

        // Pages
        if (adminStore.isGreaterThanMin(BadgeType.PAGES, userStats.pagesRead())) {
            int highestTier = getHighestTierBadgeByType(userBadges, BadgeType.PAGES);
            badgesToAdd.addAll(getBadgesToAddToDB(highestTier, userStats.pagesRead(), BadgeType.PAGES));
        }

        // Friends
        if (adminStore.isGreaterThanMin(BadgeType.FRIENDS, userStats.totalFriends())) {
            int highestTier = getHighestTierBadgeByType(userBadges, BadgeType.FRIENDS);
            badgesToAdd.addAll(getBadgesToAddToDB(highestTier, userStats.totalFriends(), BadgeType.FRIENDS));
        }

        // readingStreak
        if (adminStore.isGreaterThanMin(BadgeType.STREAK, userStats.readingStreak())) {
            int highestTier = getHighestTierBadgeByType(userBadges, BadgeType.STREAK);
            badgesToAdd.addAll(getBadgesToAddToDB(highestTier, userStats.readingStreak(), BadgeType.STREAK));
        }

        // booksRead
        if (adminStore.isGreaterThanMin(BadgeType.BOOKS, userStats.booksRead())) {
            int highestTier = getHighestTierBadgeByType(userBadges, BadgeType.BOOKS);
            badgesToAdd.addAll(getBadgesToAddToDB(highestTier, userStats.booksRead(), BadgeType.BOOKS));
        }

        // booksCollected
        if (adminStore.isGreaterThanMin(BadgeType.COLLECTOR, userStats.booksCollected())) {
            int highestTier = getHighestTierBadgeByType(userBadges, BadgeType.COLLECTOR);
            badgesToAdd.addAll(getBadgesToAddToDB(highestTier, userStats.booksCollected(), BadgeType.COLLECTOR));
        }

        // challengesCompleted
        if (adminStore.isGreaterThanMin(BadgeType.CHALLENGES, userStats.challengesCompleted())) {
            int highestTier = getHighestTierBadgeByType(userBadges, BadgeType.CHALLENGES);
            badgesToAdd.addAll(getBadgesToAddToDB(highestTier, userStats.challengesCompleted(), BadgeType.CHALLENGES));
        }

        // badgesEarned
        int badgesEarned = userStats.badgesEarned() + badgesToAdd.size();
        if (adminStore.isGreaterThanMin(BadgeType.COMPLETION, badgesEarned)) {
            int highestTier = getHighestTierBadgeByType(userBadges, BadgeType.COMPLETION);
            badgesToAdd.addAll(getBadgesToAddToDB(highestTier, badgesEarned, BadgeType.COMPLETION));
        }

        return badgesToAdd;
    }

    private List<Badge> getBadgesToAddToDB(int currTier, int userStat, BadgeType type) {
        int MAX_TIER = adminStore.getMaxTier(type);

        if (currTier == 0) {
            if (currTier >= MAX_TIER) return new ArrayList<>();
        } else if (currTier < 0) {
            throw new IllegalArgumentException("currTier cannot be less than 0");
        }

        List<Badge> badgesToAdd = new ArrayList<>();

        int[] properThresholds = adminStore.getProperThresholds(type, currTier);

        int badgesEarned = 0;
        for (int threshold : properThresholds) {
            if (userStat >= threshold) {
                badgesEarned++;
            } else {
                break;
            }
        }

        if ((currTier + badgesEarned) > MAX_TIER) return new ArrayList<>();

        for (int i = 0; i < badgesEarned; i++) {
            short nextTier = (short) (currTier + 1);
            badgesToAdd.add(adminStore.getNextBadge(type, nextTier));
            currTier += 1;
        }

        return badgesToAdd;
    }

    private List<LocalDate> getExpectedDays(LocalDate startDate) {
        LocalDate today = LocalDate.now();
        long daysBetween = DAYS.between(startDate, today);
        return Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(daysBetween + 1)
                .toList();
    }

    private int getHighestTierBadgeByType(List<UserBadge> userBadges, BadgeType badgeType) {
        return userBadges.stream()
                .filter(badge -> badge.type().getName().equalsIgnoreCase(badgeType.getName()))
                .max(Comparator.comparingInt(UserBadge::tier))
                .map(UserBadge::tier)
                .orElse((short) 0);
    }

    private Badge getHighestBadgeBadgeByType(List<Badge> userBadges, BadgeType badgeType) {
        return userBadges.stream()
                .filter(bad -> bad.type().getName().equalsIgnoreCase(badgeType.getName()))
                .max(Comparator.comparingInt(Badge::tier))
                .orElse(null);
    }

    private BadgeWithNext badgeToWithNext(Badge badge) {
        return new BadgeWithNext(
                badge.id(),
                badge.name(),
                badge.description(),
                badge.threshold(),
                badge.type(),
                badge.tier(),
                badge.imageUrl(),
                badge.pointsAwarded(),
                adminStore.getNextBadge(badge.type(), (short) (badge.tier() + 1)));
    }
}