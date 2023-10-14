package com.coderiders.gamificationservice.controller;

import com.coderiders.gamificationservice.exception.BadRequestException;
import com.coderiders.gamificationservice.models.Badge;
import com.coderiders.gamificationservice.models.db.Badges;
import com.coderiders.gamificationservice.models.db.ReadingChallenges;
import com.coderiders.gamificationservice.models.enums.ActivityAction;
import com.coderiders.gamificationservice.models.enums.ChallengeType;
import com.coderiders.gamificationservice.models.requests.SavePages;
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
    public ResponseEntity<Map<String, Map<String, Badge>>> getAllBadges() {
        return new ResponseEntity<>(adminStore.getAllBadgesByType(), HttpStatus.OK);
    }

    @GetMapping("/user/badges/{clerkId}")
    public ResponseEntity<Badges> getBadgeByID(@PathVariable String clerkId) {
        return  new ResponseEntity<>(new Badges(), HttpStatus.OK);
    }

    // TODO: CURRENTLY SAVES PAGES
    @PostMapping("/user/badges") // TODO: This one can save the badges the user earned
    public ResponseEntity<String> saveBadgeByID(@RequestBody SavePages body) {
        if (body.pagesRead() <= 0) return new ResponseEntity<>("NO PAGES TO SAVE", HttpStatus.OK);

        if(!ActivityAction.isValidActionName(body.action())) {
            throw new BadRequestException("Bad Action Given: " + body.action());
        }

        return new ResponseEntity<>(userService.updateUserPages(body), HttpStatus.OK);
    }

    @GetMapping("/challenges")
    public ResponseEntity<List<ReadingChallenges>> getAllChallenges(
            @RequestParam(name = "isLimitedTime", required = false) Boolean isLimitedTime,
            @RequestParam(name = "type", required = false) String type) {

        if (type != null) return new ResponseEntity<>(adminStore.getAllChallengesByType(ChallengeType.getChallengeTypeByName(type)), HttpStatus.OK);

        if (isLimitedTime != null) {
            return isLimitedTime
                    ? new ResponseEntity<>(adminStore.getTemporaryChallenges(), HttpStatus.OK)
                    : new ResponseEntity<>(adminStore.getPermanentChallenges(), HttpStatus.OK);
        }


        return new ResponseEntity<>(adminStore.getAllChallenges(), HttpStatus.OK);
    }
}
