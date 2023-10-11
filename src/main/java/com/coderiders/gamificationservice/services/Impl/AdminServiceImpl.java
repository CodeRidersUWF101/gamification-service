package com.coderiders.gamificationservice.services.Impl;

import com.coderiders.gamificationservice.models.db.Badges;
import com.coderiders.gamificationservice.models.db.ReadingChallenges;
import com.coderiders.gamificationservice.models.enums.BadgeType;
import com.coderiders.gamificationservice.models.enums.Tiers;
import com.coderiders.gamificationservice.repository.AdminRepository;
import com.coderiders.gamificationservice.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;

    @Override
    public List<Badges> getAllBadges() {
        return adminRepository.getAllBadges();
    }

    @Override
    public List<Badges> getAllBadgesByType(BadgeType type) {
        return adminRepository.getAllBadgesByType(type);
    }

    @Override
    public List<Badges> getAllBadgesByTier(Tiers tier) {
        return adminRepository.getAllBadgesByTier(tier);
    }

    @Override
    public Badges getBadgeById(int id) {
        return adminRepository.getBadgeById(id);
    }

    @Override
    public List<ReadingChallenges> getAllChallenges() {
        return null;
    }

    @Override
    public List<ReadingChallenges> getAllChallengesByTime(boolean isLimitedTime) {
        return adminRepository.getAllChallengesByTime(isLimitedTime);
    }
}
