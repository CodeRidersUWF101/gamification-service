package com.coderiders.gamificationservice.services.Impl;

import com.coderiders.gamificationservice.models.commonutils.models.ReadingChallenges;
import com.coderiders.gamificationservice.models.commonutils.models.enums.BadgeType;
import com.coderiders.gamificationservice.models.commonutils.models.enums.ChallengeFrequency;
import com.coderiders.gamificationservice.models.commonutils.models.records.Badge;
import com.coderiders.gamificationservice.models.commonutils.utils.ConsoleFormatter;
import com.coderiders.gamificationservice.models.db.Badges;
import com.coderiders.gamificationservice.models.db.PointsSystem;
import com.coderiders.gamificationservice.models.dto.TiersThresholdsDTO;
import com.coderiders.gamificationservice.services.AdminStore;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.coderiders.gamificationservice.models.commonutils.utils.ConsoleFormatter.printColored;


@Service
public class AdminStoreImpl implements AdminStore {

    private final Map<String, Map<Short, Badge>> badgeByType = new HashMap<>();
    private final Map<Long, Badge> badgeById = new HashMap<>();
    private final List<ReadingChallenges> challenges = new ArrayList<>();
    private final Map<String, TiersThresholdsDTO> tiersAndThresholds = new HashMap<>();

    @Override
    public void initialize(@NonNull List<Badges> badges,
                           @NonNull List<ReadingChallenges> incChallenges,
                           @NonNull List<PointsSystem> pointsSystems,
                           @NonNull List<TiersThresholdsDTO> thresholdsDTOS) {

        for (Badges badge : badges) {
            Badge currBadge = badgesToBadge(badge, pointsSystems);
            badgeByType.computeIfAbsent(badge.getType().getName(), k -> new HashMap<>())
                    .put(badge.getTier(), currBadge);
            badgeById.put(badge.getId(), currBadge);
        }
        challenges.addAll(incChallenges);

        for (TiersThresholdsDTO dto : thresholdsDTOS) {
            tiersAndThresholds.put(dto.type().getName(), dto);
        }
    }

    @Override
    public Map<String, Map<Short, Badge>> getAllBadgesByType() {
        return badgeByType;
    }

    @Override
    public List<Badge> getAllBadgesList() {
        return badgeByType.values().stream()
                .flatMap(map -> map.values().stream())
                .collect(Collectors.toList());
    }

    @Override
    public Badge getBadgeByTypeAndTier(@NonNull BadgeType type, short tier) {
        return badgeByType.getOrDefault(type.getName(), null).get(tier);
    }

    @Override
    public Badge getNextBadge(@NonNull BadgeType type, @NonNull short tier) {
        if (tier > getMaxTier(type)) return null;
        return getBadgeByTypeAndTier(type, tier);
    }

    @Override
    public Badge getBadgeById(long id) {
        return badgeById.get(id);
    }

    @Override
    public ReadingChallenges getChallengeById(long id) {
        return challenges.stream()
                .filter(item -> item.getId() == id)
                .findFirst()
                .orElseGet(ReadingChallenges::new);
    }

    @Override
    public List<ReadingChallenges> getPermanentChallenges() {
        return challenges.stream()
                .filter(challenges -> challenges.getStartDate() == null)
                .toList();
    }

    @Override
    public List<ReadingChallenges> getTemporaryChallenges() {
        return challenges.stream()
                .filter(challenges -> challenges.getStartDate() != null)
                .toList();
    }

    @Override
    public List<ReadingChallenges> getAllChallengesByType(@NonNull ChallengeFrequency type) {
        return challenges.stream()
                .filter(item -> item.getFrequency().getName().equalsIgnoreCase(type.getName()))
                .toList();
    }

    @Override
    public List<ReadingChallenges> getAllChallenges() {
        return challenges;
    }

    @Override
    public Map<String, TiersThresholdsDTO> getTiersAndThresholds() {
        return tiersAndThresholds;
    }

    @Override
    public boolean isGreaterThanMin(@NonNull BadgeType type, int value) {
        int valueToCheck = tiersAndThresholds.get(type.getName()).lowestThreshold();
        return value >= valueToCheck;
    }

    @Override
    public int[] getProperThresholds(@NonNull BadgeType type, int currLevel) {
        int [] thresholds = tiersAndThresholds.get(type.getName()).allThresholds();
        return Arrays.stream(thresholds, currLevel, thresholds.length).toArray();
    }

    @Override
    public int[] getThresholds(@NonNull BadgeType type) {
        return tiersAndThresholds.get(type.getName()).allThresholds();
    }

    @Override
    public int getMaxTier(@NonNull BadgeType type) {
        return tiersAndThresholds.get(type.getName()).highestTier();
    }

    private Badge badgesToBadge(@NonNull Badges badges, @NonNull List<PointsSystem> pointsSystem) {
        return  new Badge(
                badges.getId(),
                badges.getName(),
                badges.getDescription(),
                badges.getThreshold(),
                badges.getType(),
                badges.getTier(),
                badges.getImageUrl(),
                findPoints(badges, pointsSystem)
                );
    }

    private int findPoints(@NonNull Badges badges, @NonNull List<PointsSystem> pointsSystem) {
        for (PointsSystem ps : pointsSystem) {
            if (ps.getTier() == badges.getTier()) {
                return ps.getPointsAwarded();
            }
        }
        throw new IllegalArgumentException("Badges tier level not found in Points System");
    }

    private void printTiersAndThresholds() {
        StringBuilder sb = new StringBuilder("\n");
        tiersAndThresholds.forEach((key, value) -> {
            sb.append("Key: ").append(key).append("\n");
            sb.append("Lowest Tier: ").append(value.lowestTier()).append("\n");
            sb.append("Lowest Threshold: ").append(value.lowestThreshold()).append("\n");
            sb.append("Highest Tier: ").append(value.highestTier()).append("\n");
            sb.append("Highest Threshold: ").append(value.highestThreshold()).append("\n");
            sb.append("All Thresholds: ").append(java.util.Arrays.toString(value.allThresholds())).append("\n");
            sb.append("-------------------------------------------------\n");
        });
        printColored(sb.toString(), ConsoleFormatter.Color.PURPLE);
    }
}
