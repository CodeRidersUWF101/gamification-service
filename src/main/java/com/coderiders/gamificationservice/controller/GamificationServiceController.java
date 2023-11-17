package com.coderiders.gamificationservice.controller;

import com.coderiders.gamificationservice.exception.BadRequestException;
import com.coderiders.gamificationservice.models.commonutils.models.*;
import com.coderiders.gamificationservice.models.commonutils.models.enums.ActivityAction;
import com.coderiders.gamificationservice.models.commonutils.models.enums.ChallengeFrequency;
import com.coderiders.gamificationservice.models.commonutils.models.records.Badge;
import com.coderiders.gamificationservice.models.commonutils.models.records.UserBadge;
import com.coderiders.gamificationservice.models.commonutils.models.requests.UpdateProgress;
import com.coderiders.gamificationservice.models.commonutils.utils.ConsoleFormatter;
import com.coderiders.gamificationservice.models.requests.SaveChallenge;
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

import static com.coderiders.gamificationservice.models.commonutils.utils.ConsoleFormatter.printColored;

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
    public ResponseEntity<Integer> getPoints(@PathVariable String clerkId) {
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
    public ResponseEntity<Status> savePages(@RequestBody UpdateProgress body) {
        printColored("/pages POST ENDPOINT HIT", ConsoleFormatter.Color.PURPLE);
        if (body.getPagesRead() <= 0) {
            throw new BadRequestException("No Pages to Save");
        }

        if (!ActivityAction.isValidActionName(body.getAction())) {
            throw new BadRequestException("Bad Action: " + body.getAction());
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

        String successMessage = "Successfully saved a challenge for user: " + body.clerkId();

        return new ResponseEntity<>(successMessage, HttpStatus.OK);
    }

    @GetMapping("/challenge/{clerkId}")
    public ResponseEntity<List<UserChallengesExtraDTO>> getUserChallenges(@PathVariable String clerkId) {
        printColored("/challenge/{clerkId} GET ENDPOINT HIT", ConsoleFormatter.Color.PURPLE);
        if (clerkId == null) {
            throw new BadRequestException("No Provided Clerk Id");
        }

        return new ResponseEntity<>(userService.getUserChallenges(clerkId), HttpStatus.OK);
    }

    @GetMapping("/achievements/{clerkId}")
    public ResponseEntity<List<LatestAchievement>> getLatestUserAchievements(@PathVariable String clerkId) {
        printColored("/achievements/{clerkId} GET ENDPOINT HIT", ConsoleFormatter.Color.PURPLE);

        if (clerkId == null) {
            throw new BadRequestException("No Provided Clerk Id");
        }

        return new ResponseEntity<>(userService.getLatestUserAchievements(clerkId), HttpStatus.OK);
    }

    @PostMapping("/activity")
    public ResponseEntity<AddItem> addItemToActivity(@RequestBody AddItem itemToAdd) {
        printColored("/activity POST ENDPOINT HIT", ConsoleFormatter.Color.PURPLE);

        if (itemToAdd == null) {
            throw new BadRequestException("No Provided Item");
        }

        if (itemToAdd.getClerkId() == null) {
            throw new BadRequestException("No Provided Clerk Id");
        }

        if (!ActivityAction.isValidActionName(itemToAdd.getAction())) {
            throw new BadRequestException("Bad Action: " + itemToAdd.getAction());
        }

        return new ResponseEntity<>(userService.addItemToActivity(itemToAdd), HttpStatus.OK);
    }

    @GetMapping("/stats/singlebook")
    public ResponseEntity<SingleBookStats> getSingleBookStats(
            @RequestParam(name = "book_id") String bookId,
            @RequestParam(name = "clerk_id") String clerkId) {

        printColored("/stats/singlebook GET ENDPOINT HIT: " + bookId, ConsoleFormatter.Color.PURPLE);

        if (bookId == null) {
            throw new BadRequestException("No Provided Book Id");
        }

        if (clerkId == null) {
            throw new BadRequestException("No Provided Clerk Id");
        }

        return new ResponseEntity<>(userService.getSingleBookStats(bookId, clerkId), HttpStatus.OK);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<GamificationLeaderboard>> getLeaderboard() {
        return new ResponseEntity<>(userService.getLeaderboard(), HttpStatus.OK);
    }

    @GetMapping("/userPoints/")
    public ResponseEntity<List<GamificationLeaderboard>> getUsersPoints(@RequestParam("clerk_id") String clerk_id) {
        return new ResponseEntity<>(userService.getLeaderboardFriends(clerk_id), HttpStatus.OK);
    }
}
