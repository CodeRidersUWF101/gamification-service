package com.coderiders.gamificationservice.controller;

import com.coderiders.gamificationservice.exception.BadRequestException;
import com.coderiders.gamificationservice.models.db.Badges;
import com.coderiders.gamificationservice.models.db.ReadingChallenges;
import com.coderiders.gamificationservice.models.enums.BadgeType;
import com.coderiders.gamificationservice.models.enums.Tiers;
import com.coderiders.gamificationservice.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RefreshScope
@RequestMapping("/admin")
@RequiredArgsConstructor
public class GamificationAdminController {

    private final AdminService adminService;

    @GetMapping("/badges")
    public ResponseEntity<List<Badges>> getAllBadges() {
        return new ResponseEntity<>(adminService.getAllBadges(), HttpStatus.OK);
    }

    @GetMapping("/badges/tier/{tier}")
    public ResponseEntity<List<Badges>> getAllBadgesByTier(@PathVariable String tier) {
        try {
            Tiers tierEnum = Tiers.getTiersByName(tier);
            return new ResponseEntity<>(adminService.getAllBadgesByTier(tierEnum), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Bad Tier Type");
        }
    }

    @GetMapping("/badges/type/{type}")
    public ResponseEntity<List<Badges>> getAllBadgesByType(@PathVariable String type) {
        try {
            BadgeType bt = BadgeType.getBadgeTypeByName(type);
            return new ResponseEntity<>(adminService.getAllBadgesByType(bt), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Bad Badge Type");
        }
    }

    @GetMapping("/badges/id/{id}")
    public ResponseEntity<Badges> getAllBadgesByType(@PathVariable int id) {
        return new ResponseEntity<>(adminService.getBadgeById(id), HttpStatus.OK);
    }

//    @GetMapping("/challenges")
//    public ResponseEntity<List<ReadingChallenges>> getAllChallenges() {
//        return new ResponseEntity<>(adminService.getBadgeById(id), HttpStatus.OK);
//    }

    @GetMapping("/challenges/time/{isLimitedTime}")
    public ResponseEntity<List<ReadingChallenges>> getAllPermanentChallenges(@PathVariable boolean isLimitedTime) {
        return new ResponseEntity<>(adminService.getAllChallengesByTime(isLimitedTime), HttpStatus.OK);
    }

}
