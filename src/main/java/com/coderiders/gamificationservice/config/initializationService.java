package com.coderiders.gamificationservice.config;

import com.coderiders.gamificationservice.repository.AdminRepository;
import com.coderiders.gamificationservice.services.AdminStore;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class initializationService {

    private final AdminRepository adminRepository;
    private final AdminStore adminStore;

    @PostConstruct
    public void init() {
        adminStore.initialize(
                adminRepository.getAllBadges(),
                adminRepository.getAllChallenges(),
                adminRepository.getEntirePointsSystem());
    }
}
