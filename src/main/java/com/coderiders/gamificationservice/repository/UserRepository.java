package com.coderiders.gamificationservice.repository;
import com.coderiders.gamificationservice.models.UserStatistics;
import com.coderiders.gamificationservice.models.commonutils.models.*;
import com.coderiders.gamificationservice.models.commonutils.models.enums.ActivityAction;
import com.coderiders.gamificationservice.models.commonutils.models.enums.BadgeType;
import com.coderiders.gamificationservice.models.commonutils.models.enums.ChallengeFrequency;
import com.coderiders.gamificationservice.models.commonutils.models.records.Badge;
import com.coderiders.gamificationservice.models.commonutils.models.records.UserBadge;
import com.coderiders.gamificationservice.models.commonutils.models.records.UserChallengesDTO;
import com.coderiders.gamificationservice.models.commonutils.models.requests.UpdateProgress;
import com.coderiders.gamificationservice.models.db.ReadingLogs;
import com.coderiders.gamificationservice.models.dto.UserActivityDTO;
import com.coderiders.gamificationservice.utilities.AdminQueries;
import com.coderiders.gamificationservice.utilities.Queries;
import com.coderiders.gamificationservice.utilities.QueryParam;
import com.coderiders.gamificationservice.utilities.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public void saveReadingLog(UpdateProgress pages, ActivityAction action) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(QueryParam.FIRST.getName(), pages.getClerkId());
        params.addValue(QueryParam.SECOND.getName(), pages.getPagesRead());
        params.addValue(QueryParam.THIRD.getName(), pages.getBookId());
        params.addValue(QueryParam.FOURTH.getName(), action.getName());

        jdbcTemplate.update(Queries.savePages, params);
    }

    public Integer getUserPoints(String clerkId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(QueryParam.FIRST.getName(), clerkId);
        return jdbcTemplate.queryForObject(Queries.getUserPoints, params, Integer.class);
    }

    public UserStatistics getUserStatistics(String clerkId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(QueryParam.FIRST.getName(), clerkId);
        return jdbcTemplate.queryForObject(Queries.getUserStatistics, params, (rs, rowNum) ->
                new UserStatistics(rs.getInt("total_pages_read"),
                        rs.getInt("total_challenges_completed"),
                        rs.getInt("total_badges_earned"),
                        rs.getInt("total_books_read"),
                        rs.getInt("total_friends"),
                        rs.getInt("total_books_collected"),
                        -1, false));
    }

    public List<ReadingLogs> getUserReadingLogs(String clerkId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(QueryParam.FIRST.getName(), clerkId);
        return jdbcTemplate.query(Queries.getUserReadingLogs, params, (rs, rowNum) ->
                ReadingLogs.builder()
                        .id(rs.getLong("id"))
                        .clerkId(rs.getString("clerk_id"))
                        .date(Utils.convertToLocalDateTime(rs.getTimestamp("date")))
                        .pagesRead(rs.getInt("pages_read"))
                        .bookId(rs.getString("book_id"))
                        .action(ActivityAction.getActivityActionByName(rs.getString("action")))
                        .build());
    }

    public List<UserBadge> getUserBadges(String clerkId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(QueryParam.FIRST.getName(), clerkId);
        return jdbcTemplate.query(Queries.getUserBadgeExpended, params, (rs, rowNum) ->
                new UserBadge(rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("threshold"),
                        BadgeType.getBadgeTypeByName(rs.getString("type")),
                        (rs.getShort("tier")),
                        rs.getString("image_url"),
                        rs.getInt("points_awarded"),
                        Utils.convertToLocalDateTime(rs.getTimestamp("date_earned")),
                        null));
    }

    public List<UserChallengesDTO> getUserChallenges(String clerkId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(QueryParam.FIRST.getName(), clerkId);
        return jdbcTemplate.query(Queries.getUserChallengesExpanded, params, (rs, rowNum) ->
                new UserChallengesDTO(
                        rs.getLong("id"),
                        rs.getLong("user_challenge_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        ChallengeFrequency.getChallengeTypeByName(rs.getString("frequency")),
                        BadgeType.getBadgeTypeByName(rs.getString("type")),
                        rs.getInt("threshold"),
                        rs.getInt("duration"),
                        Utils.convertToLocalDateTime(rs.getTimestamp("challengeStartDate")),
                        Utils.convertToLocalDateTime(rs.getTimestamp("challengeEndDate")),
                        rs.getInt("points_awarded"),
                        Utils.convertToLocalDateTime(rs.getTimestamp("UserChallengeStartDate")),
                        ActivityAction.getActivityActionByName(rs.getString("status"))));
    }

    public void addBadgesToUser(String clerkId, List<Badge> badgesToAdd) {
        List<SqlParameterSource> parameters = new ArrayList<>();

        for (Badge badge : badgesToAdd) {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue(QueryParam.FIRST.getName(), clerkId);
            params.addValue(QueryParam.SECOND.getName(), badge.getId());
            parameters.add(params);
        }

        jdbcTemplate.batchUpdate(Queries.saveUserBadges, parameters.toArray(new SqlParameterSource[0]));
    }

    public void updateUserChallenges(List<UserChallengesExtraDTO> challenges, String clerkId) {
        List<SqlParameterSource> parameters = new ArrayList<>();

        for (UserChallengesExtraDTO challenge : challenges) {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue(QueryParam.FIRST.getName(), challenge.getStatus());
            params.addValue(QueryParam.SECOND.getName(), LocalDateTime.now());
            params.addValue(QueryParam.THIRD.getName(), clerkId);
            params.addValue(QueryParam.FOURTH.getName(), challenge.getUserChallengeId());
            parameters.add(params);
        }

        jdbcTemplate.batchUpdate(Queries.updateUserChallenges, parameters.toArray(new SqlParameterSource[0]));
    }

    public void saveSingleUserActivity(String clerkId, ActivityAction action, Integer actionId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(QueryParam.FIRST.getName(), clerkId);
        params.addValue(QueryParam.SECOND.getName(), action.getName());
        params.addValue(QueryParam.THIRD.getName(), actionId);
        jdbcTemplate.update(Queries.saveUserActivity, params);
    }

    public void saveManyUserActivity(List<UserActivityDTO> userActivity) {
        List<SqlParameterSource> parameters = new ArrayList<>();

        for (UserActivityDTO dto : userActivity) {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue(QueryParam.FIRST.getName(), dto.clerkId());
            params.addValue(QueryParam.SECOND.getName(), dto.action().getName());
            params.addValue(QueryParam.THIRD.getName(), dto.actionId());
            parameters.add(params);
        }

       jdbcTemplate.batchUpdate(Queries.saveUserActivity, parameters.toArray(new SqlParameterSource[0]));
    }

    public void saveUserChallenge(String clerkId, int challengeId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(QueryParam.FIRST.getName(), clerkId);
        params.addValue(QueryParam.SECOND.getName(), challengeId);
        jdbcTemplate.update(Queries.saveUserChallenge, params);
    }

    public List<LatestAchievement> getLatestUserAchievements(String clerkId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(QueryParam.FIRST.getName(), clerkId);
        return jdbcTemplate.query(Queries.getLatestAchievements, params, (rs, rowNum) ->
                new LatestAchievement(
                        rs.getString("name"),
                        rs.getString("elementtype"),
                        rs.getString("description"),
                        rs.getInt("threshold"),
                        Utils.convertToLocalDateTime(rs.getTimestamp("date"))));
    }

    public List<BookStats> getSingleBookStats(String clerkId, String bookId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(QueryParam.FIRST.getName(), clerkId);
        params.addValue(QueryParam.SECOND.getName(), bookId);
        return jdbcTemplate.query(Queries.getSinglePageStats, params, (rs, rowNum) ->
                new BookStats(
                        rs.getInt("pages_read"),
                        Utils.convertToLocalDateTime(rs.getTimestamp("date"))));
    }

    public List<GamificationLeaderboard> getLeaderboard() {
        return jdbcTemplate.query(Queries.getLeaderboard, (rs, rowNum) ->
                new GamificationLeaderboard(rs.getString("clerk_id"), rs.getInt("TotalPoints")));
    }

//    public List<GamificationLeaderboard> getLeaderboardFriends(String clerk_id) {
//        MapSqlParameterSource params = new MapSqlParameterSource();
//        params.addValue("first", clerk_id);
//        return jdbcTemplate.query(Queries.findFriendPointsForLeaderboard, params, (rs, rowNum) ->
//                new GamificationLeaderboard(rs.getString("clerk_id"), rs.getInt("TotalPoints")));
//
//    }

    public List<GamificationLeaderboard> getLeaderboardFriends(List<UtilsUser> usersToSearch) {
        List<String> clerkIds = usersToSearch.stream()
                .map(UtilsUser::getClerkId)
                .toList();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userIds", clerkIds);

        Map<String, Integer> results = jdbcTemplate.query(
                Queries.findFriendPointsForLeaderboard,
                params,
                rs -> {
                    Map<String, Integer> map = new HashMap<>();
                    while (rs.next()) {
                        map.put(rs.getString("clerk_id"), rs.getInt("TotalPoints"));
                    }
                    return map;
                });

        List<GamificationLeaderboard> leaderboard = new ArrayList<>();
        for (UtilsUser user : usersToSearch) {
            Integer points = results.getOrDefault(user.getClerkId(), 0);
            leaderboard.add(new GamificationLeaderboard(user.getClerkId(), points));
        }
        return leaderboard;
    }


}
