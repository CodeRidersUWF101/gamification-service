package com.coderiders.gamificationservice.controller;

import com.coderiders.gamificationservice.exception.BadRequestException;
import com.coderiders.gamificationservice.models.Badge;
import com.coderiders.gamificationservice.models.db.Badges;
import com.coderiders.gamificationservice.models.db.ReadingChallenges;
import com.coderiders.gamificationservice.models.enums.ActivityAction;
import com.coderiders.gamificationservice.models.enums.ChallengeFrequency;
import com.coderiders.gamificationservice.models.requests.SaveChallenge;
import com.coderiders.gamificationservice.models.requests.SavePages;
import com.coderiders.gamificationservice.models.responses.Status;
import com.coderiders.gamificationservice.repository.UserRepository;
import com.coderiders.gamificationservice.services.AdminStore;
import com.coderiders.gamificationservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RefreshScope
@RequiredArgsConstructor
public class GamificationServiceController {

    private final AdminStore adminStore;
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<String> helloGamification() {
        return new ResponseEntity<>("Gamification Service", HttpStatus.OK);
    }

    @GetMapping("/test/{clerkId}")
    public ResponseEntity<Integer> helloGamification(@PathVariable String clerkId) {
        return new ResponseEntity<>(userRepository.getUserPoints(clerkId), HttpStatus.OK);
    }

    @GetMapping("/badges")
    public ResponseEntity<Map<String, Map<Short, Badge>>> getAllBadges() {
        return new ResponseEntity<>(adminStore.getAllBadgesByType(), HttpStatus.OK);
    }

    @GetMapping("/user/badges/{clerkId}")
    public ResponseEntity<Badges> getBadgeByID(@PathVariable String clerkId) {
        return  new ResponseEntity<>(new Badges(), HttpStatus.OK);
    }

    @PostMapping("/pages")
    public ResponseEntity<Status> saveBadgeByID(@RequestBody SavePages body) {
        if (body.pagesRead() <= 0) {
            throw new BadRequestException("No Pages to Save");
        }

        if(!ActivityAction.isValidActionName(body.action())) {
            throw new BadRequestException("Bad Action: " + body.action());
        }

        return new ResponseEntity<>(userService.updateUserPages(body), HttpStatus.OK);
    }

    @GetMapping("/challenges")
    public ResponseEntity<List<ReadingChallenges>> getAllChallenges(
            @RequestParam(name = "isLimitedTime", required = false) Boolean isLimitedTime,
            @RequestParam(name = "type", required = false) String type) {

        if (type != null) return new ResponseEntity<>(adminStore.getAllChallengesByType(ChallengeFrequency.getChallengeTypeByName(type)), HttpStatus.OK);

        if (isLimitedTime != null) {
            return isLimitedTime
                    ? new ResponseEntity<>(adminStore.getTemporaryChallenges(), HttpStatus.OK)
                    : new ResponseEntity<>(adminStore.getPermanentChallenges(), HttpStatus.OK);
        }


        return new ResponseEntity<>(adminStore.getAllChallenges(), HttpStatus.OK);
    }

    @PostMapping("/gamification/challenge")
    public ResponseEntity<String> saveChallenge(@RequestBody SaveChallenge body) {

        if (body.challengeId() <= 0) return new ResponseEntity<>("INVALID ID", HttpStatus.OK);

        if (body.clerkId() != null) {
            userRepository.saveUserChallenge(body.clerkId(), body.challengeId());
        }

        String successMessage = "Successfully saved a challenge for user: " + body.clerkId();

        return new ResponseEntity<>(successMessage, HttpStatus.OK);
    }
}
