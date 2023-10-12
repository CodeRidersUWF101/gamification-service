package com.coderiders.gamificationservice.services.Impl;

import com.coderiders.gamificationservice.models.Badge;
import com.coderiders.gamificationservice.models.db.Badges;
import com.coderiders.gamificationservice.models.db.PointsSystem;
import com.coderiders.gamificationservice.models.db.ReadingChallenges;
import com.coderiders.gamificationservice.models.enums.BadgeType;
import com.coderiders.gamificationservice.models.enums.ChallengeType;
import com.coderiders.gamificationservice.models.enums.Tiers;
import com.coderiders.gamificationservice.services.AdminStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminStoreImpl implements AdminStore {

    private final Map<String, Map<String, Badge>> badgeByType = new HashMap<>();
    private final Map<Long, Badge> badgeById = new HashMap<>();
    private final List<ReadingChallenges> challenges = new ArrayList<>();

    @Override
    public void initialize(List<Badges> badges, List<ReadingChallenges> incChallenges, List<PointsSystem> pointsSystems) {
        for (Badges badge : badges) {
            Badge currBadge = badgesToBadge(badge, pointsSystems);
            badgeByType.computeIfAbsent(badge.getType().getName(), k -> new HashMap<>())
                    .put(badge.getTier().getName(), currBadge);
            badgeById.put(badge.getId(), currBadge);
        }
        challenges.addAll(incChallenges);
    }

    @Override
    public Map<String, Map<String, Badge>> getAllBadgesByType() {
        return badgeByType;
    }

    @Override
    public List<Badge> getAllBadgesList() {
        return badgeByType.values().stream()
                .flatMap(map -> map.values().stream())
                .collect(Collectors.toList());
    }

    @Override
    public Badge getBadgeByTypeAndTier(BadgeType type, Tiers tier) {
        return badgeByType.getOrDefault(type.getName(), null).get(tier.getName());
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
    public List<ReadingChallenges> getAllChallengesByType(ChallengeType type) {
        return challenges.stream()
                .filter(item -> item.getType().getName().equalsIgnoreCase(type.getName()))
                .toList();
    }

    @Override
    public List<ReadingChallenges> getAllChallenges() {
        return challenges;
    }

    private Badge badgesToBadge(Badges badges, List<PointsSystem> pointsSystem) {
        return  new Badge(
                badges.getId(),
                badges.getName(),
                badges.getDescription(),
                badges.getThreshold(),
                badges.getType(),
                badges.getTier(),
                badges.getTier().getValue(),
                badges.getImageUrl(),
                findPoints(badges, pointsSystem)
                );
    }

    private int findPoints(Badges badges, List<PointsSystem> pointsSystem) {
        for (PointsSystem ps : pointsSystem) {
            if (ps.getTierLevel().equals(badges.getTier())) {
                return ps.getPointsAwarded();
            }
        }
        throw new IllegalArgumentException("Badges tier level not found in Points System");
    }
}
