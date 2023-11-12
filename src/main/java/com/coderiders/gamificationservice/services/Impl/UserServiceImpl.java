package com.coderiders.gamificationservice.services.Impl;

import com.coderiders.gamificationservice.models.UserStatistics;
import com.coderiders.gamificationservice.models.commonutils.models.*;
import com.coderiders.gamificationservice.models.commonutils.models.enums.ActivityAction;
import com.coderiders.gamificationservice.models.commonutils.models.enums.BadgeType;
import com.coderiders.gamificationservice.models.commonutils.models.records.*;
import com.coderiders.gamificationservice.models.commonutils.models.requests.UpdateProgress;
import com.coderiders.gamificationservice.models.commonutils.utils.ConsoleFormatter;
import com.coderiders.gamificationservice.models.db.ReadingLogs;
import com.coderiders.gamificationservice.repository.UserRepository;
import com.coderiders.gamificationservice.services.AdminStore;
import com.coderiders.gamificationservice.services.UserService;
import com.coderiders.gamificationservice.utilities.Constants;
import com.coderiders.gamificationservice.utilities.ReadingStreak;
import com.coderiders.gamificationservice.utilities.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.coderiders.gamificationservice.models.commonutils.utils.ConsoleFormatter.printColored;
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
    public Status updateUserPages(UpdateProgress pages) {
        ActivityAction userAction = ActivityAction.getActivityActionByName(pages.getAction());

        // get all user badges
        List<UserBadge> userBadges = userRepository.getUserBadges(pages.getClerkId());

        // Add pages to ReadingLog
        userRepository.saveReadingLog(pages, userAction);
        List<ReadingLogs> logs = getUserReadingLogs(pages.getClerkId());

        // Get User Statistics
        UserStatistics stats = getUserStatistics(pages.getClerkId(), logs);

        List<Badge> badgesEarned = determineUserBadges(userBadges, stats);

        // if badgesEarned has items
        if (!badgesEarned.isEmpty()) {
            userRepository.addBadgesToUser(pages.getClerkId(), badgesEarned);
        }

        // Check if user is enrolled in a challenge
        List<UserChallengesDTO> userChallenges = userRepository.getUserChallenges(pages.getClerkId());

        List<UserChallengesExtraDTO> updatedChallenges = determineUserChallenges(userChallenges, logs);

        List<UserChallengesExtraDTO> challengesToUpdate = updatedChallenges.stream()
                .filter(item -> validChallengeSaveActions.contains(item.getStatus()))
                .toList();

        userRepository.updateUserChallenges(challengesToUpdate, pages.getClerkId());

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

        Map<Long, UserChallengesExtraDTO> updatedChallengesMap = determineUserChallenges(userChallenges, logs)
                .stream()
                .filter(item -> ActivityAction.STARTED_CHALLENGE.getName().equalsIgnoreCase(item.getStatus()))
                .collect(Collectors.toMap(UserChallengesExtraDTO::getId, Function.identity()));

        Set<Long> challengeIds = updatedChallengesMap.keySet();

        return adminStore.getAllChallenges().stream()
                .map(Utils::readingChallengeToDTO)
                .map(chall -> challengeIds.contains(chall.getId()) ? updatedChallengesMap.get(chall.getId()) : chall)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<UserBadge>> getUserBadges(String clerkId) {
        List<UserBadge> userBadges = userRepository.getUserBadges(clerkId);
        Map<Long, UserBadge> userBadgeMap = userBadges.stream().collect(Collectors.toMap(UserBadge::getId, Function.identity()));

        List<ReadingLogs> logs = getUserReadingLogs(clerkId);
        UserStatistics stats = getUserStatistics(clerkId, logs);

        Map<String, List<UserBadge>> returnBadges = adminStore.getAllBadgesList().stream()
                .map(this::badgeToUserBadge)
                .map(item -> userBadgeMap.getOrDefault(item.getId(), item))
                .collect(Collectors.groupingBy(badge -> badge.getType().getName()));

        return determineBadgeProgress(returnBadges, stats);
    }

    @Override
    public AddItem addItemToActivity(AddItem addItem) {

        userRepository.saveSingleUserActivity(addItem.getClerkId(),
                ActivityAction.getActivityActionByName(addItem.getAction()),
                null);

        return addItem;
    }

    @Override
    public List<LatestAchievement> getLatestUserAchievements(String clerkId) {
        return userRepository.getLatestUserAchievements(clerkId);
    }

    @Override
    public SingleBookStats getSingleBookStats(String bookId, String clerkId) {
        List<BookStats> bookStats = userRepository.getSingleBookStats(clerkId, bookId);

        Map<LocalDate, Integer> aggregatedStats = bookStats.stream()
                .collect(Collectors.groupingBy(
                        stat -> stat.getDateRead().toLocalDate(),
                        Collectors.summingInt(BookStats::getPagesRead)
                ));

        List<BookStats> aggregatedBookStats = new ArrayList<>();
        for (Map.Entry<LocalDate, Integer> entry : aggregatedStats.entrySet()) {
            BookStats stat = new BookStats();
            stat.setDateRead(entry.getKey().atStartOfDay());
            stat.setPagesRead(entry.getValue());
            aggregatedBookStats.add(stat);
        }
        long numOfDays = aggregatedStats.keySet().size();

        Status status = new Status();
        if (numOfDays < 3) {
            status.setStatusCode(Constants.MORE_DAYS_CODE);
            status.setStatusDescription(Constants.MORE_DAYS_DESC);
        } else {
            status.setStatusCode(Constants.SUCCESS);
            status.setStatusDescription(Constants.SUCCESS_DESC);
        }

        return new SingleBookStats(aggregatedBookStats, status);
    }

    @Override
    public List<GamificationLeaderboard> getLeaderboard() {
        return userRepository.getLeaderboard();
    }

    private Map<String, List<UserBadge>> determineBadgeProgress(Map<String, List<UserBadge>> badges, UserStatistics stats) {
        for (Map.Entry<String, List<UserBadge>> entry : badges.entrySet()) {
            String key = entry.getKey();
            List<UserBadge> values = entry.getValue();

            int highestTierEarned = -1;
            for (int i = 0; i < values.size(); i++) {
                UserBadge currBadge = values.get(i);
                if (currBadge.getDateEarned() != null) { highestTierEarned = i; }
            }

            int highestTierForBadgeType = adminStore.getMaxTier(BadgeType.getBadgeTypeByName(key));
            if (highestTierEarned >= (highestTierForBadgeType - 1)) { continue; }

            // Pages
            if (BadgeType.PAGES.getName().equalsIgnoreCase(key)) {
                UserBadge currBadge = values.get(highestTierEarned + 1);
                currBadge.setAdditionalBadgeInfo(getBadgeInfo(currBadge, stats.pagesRead()));
                values.set(highestTierEarned + 1, currBadge);
            }

            // Friends
            if (BadgeType.FRIENDS.getName().equalsIgnoreCase(key)) {
                UserBadge currBadge = values.get(highestTierEarned + 1);
                currBadge.setAdditionalBadgeInfo(getBadgeInfo(currBadge, stats.totalFriends()));
                values.set(highestTierEarned + 1, currBadge);
            }

            // readingStreak
            if (BadgeType.STREAK.getName().equalsIgnoreCase(key)) {
                UserBadge currBadge = values.get(highestTierEarned + 1);
                currBadge.setAdditionalBadgeInfo(getBadgeInfo(currBadge, stats.readingStreak()));
                values.set(highestTierEarned + 1, currBadge);
            }

            // booksRead
            if (BadgeType.BOOKS.getName().equalsIgnoreCase(key)) {
                UserBadge currBadge = values.get(highestTierEarned + 1);
                currBadge.setAdditionalBadgeInfo(getBadgeInfo(currBadge, stats.booksRead()));
                values.set(highestTierEarned + 1, currBadge);
            }

            // booksCollected
            if (BadgeType.COLLECTOR.getName().equalsIgnoreCase(key)) {
                UserBadge currBadge = values.get(highestTierEarned + 1);
                currBadge.setAdditionalBadgeInfo(getBadgeInfo(currBadge, stats.booksCollected()));
                values.set(highestTierEarned + 1, currBadge);
            }

            // challengesCompleted
            if (BadgeType.CHALLENGES.getName().equalsIgnoreCase(key)) {
                UserBadge currBadge = values.get(highestTierEarned + 1);
                currBadge.setAdditionalBadgeInfo(getBadgeInfo(currBadge, stats.challengesCompleted()));
                values.set(highestTierEarned + 1, currBadge);
            }

            // reviews
            if (BadgeType.ENGAGEMENT.getName().equalsIgnoreCase(key)) {
                UserBadge currBadge = values.get(highestTierEarned + 1);
                currBadge.setAdditionalBadgeInfo(getBadgeInfo(currBadge, null));
                values.set(highestTierEarned + 1, currBadge);
            }

            // badgesEarned
            if (BadgeType.COMPLETION.getName().equalsIgnoreCase(key)) {
                UserBadge currBadge = values.get(highestTierEarned + 1);
                currBadge.setAdditionalBadgeInfo(getBadgeInfo(currBadge, stats.badgesEarned()));
                values.set(highestTierEarned + 1, currBadge);
            }
        }
        return badges;
    }

    private AdditionalChallengeInfo getBadgeInfo(UserBadge badge, Integer complete) {
        if (complete == null) {
            return new AdditionalChallengeInfo(0.0, 1.0, 0.0);
        }

        double toGo = badge.getThreshold() - complete;
        double percentComplete = ((double) complete / badge.getThreshold()) * 100;
        return  new AdditionalChallengeInfo((double) complete, toGo, percentComplete);
    }

    private UserBadge badgeToUserBadge(Badge badge) {
        return new UserBadge(
                badge.getId(),
                badge.getName(),
                badge.getDescription(),
                badge.getThreshold(),
                BadgeType.getBadgeTypeByName(badge.getType().getName()),
                badge.getTier(),
                badge.getImageUrl(),
                badge.getPointsAwarded(),
                null, null);
    }

    private Status prepareReturnStatus(List<Badge> badgesEarned, List<UserChallengesExtraDTO> challengedCompleted) {
        List<BadgeWithNext> returnBadges = badgesEarned.stream().map(this::badgeToWithNext).toList();

        return new Status(Constants.SUCCESS, Constants.SUCCESS_DESC, null, returnBadges, challengedCompleted);
    }

    // TODO: Find a better way to do this.
    private List<UserChallengesExtraDTO> determineUserChallenges(List<UserChallengesDTO> userChallenges, List<ReadingLogs> logs) {
        List<UserChallengesExtraDTO> returnChallenges = new ArrayList<>();

        for (UserChallengesDTO challenge : userChallenges) {

            // if startDate or EndDate is null, it's a permanent event and needs to be determined by user.
            if (challenge.getChallengeStartDate() == null || challenge.getChallengeEndDate() == null) {
                LocalDateTime userChallengeStartDate = challenge.getUserChallengeStartDate();
                LocalDateTime userEndDate = userChallengeStartDate.plusDays(challenge.getDuration() - 1);

                Predicate<ReadingLogs> filteredLogsToProperRange = log ->
                        (log.getDate().isAfter(userChallengeStartDate) || log.getDate().isEqual(userChallengeStartDate))
                        && (log.getDate().isBefore(userEndDate.plusDays(1)) || log.getDate().isEqual(userEndDate));

                Stream<ReadingLogs> filteredLogs = logs.stream().filter(filteredLogsToProperRange);

                UserChallengesExtraDTO updatedChallenge = switch (challenge.getType()) {
                    case STREAK -> checkStreak(filteredLogs, challenge);
                    case PAGES -> checkPages(filteredLogs, challenge);
                    case BOOKS -> checkBooksRead(filteredLogs, challenge);
                    default -> null;
                };

                if (updatedChallenge != null) {
                    updatedChallenge.setUserChallengeEndDate(String.valueOf(userEndDate));
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
        UserChallengesExtraDTO updatedChallenge = Utils.userChallengesToDTO(challenge);
        int challengeThreshold = challenge.getThreshold();

        LocalDate today = LocalDate.now();
        LocalDate userChallengeStartDate = challenge.getUserChallengeStartDate().toLocalDate();
        LocalDate userEndDate = userChallengeStartDate.plusDays(challenge.getDuration() - 1);

        if (today.isBefore(userEndDate) || today.isEqual(userEndDate)) {

            long totalDays = DAYS.between(userChallengeStartDate, userEndDate.plusDays(1));
            int daysCompleted = (int) DAYS.between(userChallengeStartDate.minusDays(1), today);
            int remainingDays = (int) DAYS.between(today, userEndDate);
            double percentageComplete = ((double) daysCompleted / totalDays) * 100;

            // User has completed the challenge.
            if (userActual >= challengeThreshold) {

                updatedChallenge.setStatus(ActivityAction.COMPLETED_CHALLENGE.getName());
                DateProgress dateProgress = new DateProgress(percentageComplete, daysCompleted, remainingDays);
                updatedChallenge.setDateProgress(dateProgress);

            } else { // Challenge Ongoing

                double percentageOfChallengeComplete = ((double) userActual / challengeThreshold) * 100;
                double pagesToGo = challengeThreshold - userActual;

                AdditionalChallengeInfo additionalInfo = new AdditionalChallengeInfo(
                        (double) userActual,
                        pagesToGo,
                        percentageOfChallengeComplete);

                updatedChallenge.setAdditionalInfo(additionalInfo);
                updatedChallenge.setStatus(ActivityAction.STARTED_CHALLENGE.getName());
                DateProgress dateProgress = new DateProgress(percentageComplete, daysCompleted, remainingDays);
                updatedChallenge.setDateProgress(dateProgress);
            }
            return updatedChallenge;
        }

        // User has failed the challenge.
        updatedChallenge.setStatus(ActivityAction.FAILED_CHALLENGE.getName());
        return updatedChallenge;
    }

    private UserChallengesExtraDTO checkStreak(Stream<ReadingLogs> filteredLogs, UserChallengesDTO challenge) {

        LocalDate today = LocalDate.now();
        LocalDate userChallengeStartDate = challenge.getUserChallengeStartDate().toLocalDate();
        LocalDate userEndDate = userChallengeStartDate.plusDays(challenge.getDuration() - 1);

        UserChallengesExtraDTO updatedChallenge = Utils.userChallengesToDTO(challenge);

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
        double daysCompleted = DAYS.between(userChallengeStartDate.minusDays(1), today);
        double remainingDays = DAYS.between(today, userEndDate);
        double percentageComplete = (daysCompleted / totalDays) * 100;

        if (hasLogForEachDay) {  // At least one log for exists for each day exists
            if (today.isAfter(userEndDate) || today.isEqual(userEndDate)) { // User has completed the challenge.
                updatedChallenge.setStatus(ActivityAction.COMPLETED_CHALLENGE.getName());

            } else { // User has not failed but is not over yet.
                AdditionalChallengeInfo additionalInfo = new AdditionalChallengeInfo(
                        daysCompleted,
                        remainingDays,
                        percentageComplete);

                updatedChallenge.setAdditionalInfo(additionalInfo);
            }

            DateProgress dateProgress = new DateProgress(percentageComplete, (int) daysCompleted, (int) remainingDays);
            updatedChallenge.setDateProgress(dateProgress);

            return updatedChallenge;
        }

        updatedChallenge.setStatus(ActivityAction.FAILED_CHALLENGE.getName());
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
                .filter(badge -> badge.getType().getName().equalsIgnoreCase(badgeType.getName()))
                .max(Comparator.comparingInt(UserBadge::getTier))
                .map(UserBadge::getTier)
                .orElse((short) 0);
    }

    private Badge getHighestBadgeBadgeByType(List<Badge> userBadges, BadgeType badgeType) {
        return userBadges.stream()
                .filter(bad -> bad.getType().getName().equalsIgnoreCase(badgeType.getName()))
                .max(Comparator.comparingInt(Badge::getTier))
                .orElse(null);
    }

    private BadgeWithNext badgeToWithNext(Badge badge) {
        return new BadgeWithNext(
                badge.getId(),
                badge.getName(),
                badge.getDescription(),
                badge.getThreshold(),
                badge.getType(),
                badge.getTier(),
                badge.getImageUrl(),
                badge.getPointsAwarded(),
                adminStore.getNextBadge(badge.getType(), (short) (badge.getTier() + 1)));
    }
}