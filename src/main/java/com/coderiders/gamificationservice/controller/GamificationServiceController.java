package com.coderiders.gamificationservice.controller;

import com.coderiders.commonutils.models.records.UserBadge;
import com.coderiders.commonutils.utils.ConsoleFormatter;
import com.coderiders.gamificationservice.exception.BadRequestException;
import com.coderiders.gamificationservice.models.Badge;
import com.coderiders.gamificationservice.models.db.ReadingChallenges;
import com.coderiders.gamificationservice.models.enums.ActivityAction;
import com.coderiders.gamificationservice.models.enums.ChallengeFrequency;
import com.coderiders.gamificationservice.models.requests.SaveChallenge;
import com.coderiders.gamificationservice.models.requests.SavePages;
import com.coderiders.gamificationservice.models.responses.Status;
import com.coderiders.gamificationservice.models.responses.UserChallengesExtraDTO;
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

import static com.coderiders.commonutils.utils.ConsoleFormatter.printColored;

@RestController
@RefreshScope
@RequestMapping("/gamification")
@RequiredArgsConstructor
public class GamificationServiceController {

    private final AdminStore adminStore;
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<String> helloGamification() {
        return new ResponseEntity<>("Gamification Service", HttpStatus.OK);
    }

    @GetMapping("/points/{clerkId}")
    public ResponseEntity<Integer> helloGamification(@PathVariable String clerkId) {
        return new ResponseEntity<>(userRepository.getUserPoints(clerkId), HttpStatus.OK);
    }

    @GetMapping("/badges")
    public ResponseEntity<Map<String, Map<Short, Badge>>> getAllBadges() {
        return new ResponseEntity<>(adminStore.getAllBadgesByType(), HttpStatus.OK);
    }

    @GetMapping("/badges/{clerkId}")
    public ResponseEntity<Map<String, List<UserBadge>>> getAllBadges(@PathVariable String clerkId) {
        printColored("/badges/{clerkId} POST ENDPOINT HIT", ConsoleFormatter.Color.PURPLE);
        return new ResponseEntity<>(userService.getUserBadges(clerkId), HttpStatus.OK);
    }

    @PostMapping("/pages")
    public ResponseEntity<Status> saveBadgeByID(@RequestBody SavePages body) {
        printColored("/pages POST ENDPOINT HIT", ConsoleFormatter.Color.PURPLE);
        if (body.pagesRead() <= 0) {
            throw new BadRequestException("No Pages to Save");
        }

        if(!ActivityAction.isValidActionName(body.action())) {
            throw new BadRequestException("Bad Action: " + body.action());
        }

        return new ResponseEntity<>(userService.updateUserPages(body), HttpStatus.OK);
    }

    @GetMapping("/challenge")
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


    @PostMapping("/challenge")
    public ResponseEntity<String> saveChallenge(@RequestBody SaveChallenge body) {
        printColored("/challenge POST ENDPOINT HIT", ConsoleFormatter.Color.PURPLE);
        if (body.challengeId() <= 0) {
            throw new BadRequestException("INVALID ID" + body.challengeId());
        }

        if (body.clerkId() == null) {
            throw new BadRequestException("No Provided Clerk Id");
        }

        userRepository.saveUserChallenge(body.clerkId(), body.challengeId());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/challenge/{clerkId}")
    public ResponseEntity<List<UserChallengesExtraDTO>> getUserChallenges(@PathVariable String clerkId) {
        printColored("/challenge/{clerkId} GET ENDPOINT HIT", ConsoleFormatter.Color.PURPLE);
        if (clerkId == null) {
            throw new BadRequestException("No Provided Clerk Id");
        }

        return new ResponseEntity<>(userService.getUserChallenges(clerkId), HttpStatus.OK);
    }

}
