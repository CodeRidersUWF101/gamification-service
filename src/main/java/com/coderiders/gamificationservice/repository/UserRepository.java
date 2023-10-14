package com.coderiders.gamificationservice.repository;

import com.coderiders.gamificationservice.models.Badge;
import com.coderiders.gamificationservice.models.UserStatistics;
import com.coderiders.gamificationservice.models.db.ReadingLogs;
import com.coderiders.gamificationservice.models.db.UserChallenges;
import com.coderiders.gamificationservice.models.dto.UserActivityDTO;
import com.coderiders.gamificationservice.models.dto.UserPointsDTO;
import com.coderiders.gamificationservice.models.enums.ActivityAction;
import com.coderiders.gamificationservice.models.enums.BadgeType;
import com.coderiders.gamificationservice.models.enums.Tiers;
import com.coderiders.gamificationservice.models.enums.UserChallengeStatus;
import com.coderiders.gamificationservice.models.requests.SavePages;
import com.coderiders.gamificationservice.services.AdminStore;
import com.coderiders.gamificationservice.utilities.Queries;
import com.coderiders.gamificationservice.utilities.QueryParam;
import com.coderiders.gamificationservice.utilities.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final AdminStore adminStore;


    public long saveReadingLog(SavePages pages) { // TODO: ADD ACTION TO QUERY. Defaults to READING_PAGES, Need to update for COMPLETED_BOOK or STARTED_BOOK.
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(QueryParam.FIRST.getName(), pages.clerkId());
        params.addValue(QueryParam.SECOND.getName(), pages.pagesRead());
        params.addValue(QueryParam.THIRD.getName(), pages.bookId());
        return jdbcTemplate.update(Queries.savePages, params);
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
                        .build());
    }

    public List<Badge> getUserBadges(String clerkId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(QueryParam.FIRST.getName(), clerkId);
        return jdbcTemplate.query(Queries.getUserBadgeExpended, params, (rs, rowNum) ->
                new Badge(rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("threshold"),
                        BadgeType.getBadgeTypeByName(rs.getString("type")),
                        Tiers.getTiersByName(rs.getString("tier")),
                        Tiers.getTiersByName(rs.getString("tier")).getValue(),
                        rs.getString("image_url"),
                        rs.getInt("points_awarded")));
    }

//    public List<UserBadges> getUserBadges(String clerkId) {
//        MapSqlParameterSource params = new MapSqlParameterSource();
//        params.addValue(QueryParam.FIRST.getName(), clerkId);
//        return jdbcTemplate.query(Queries.getAllUserBadges, params, (rs, rowNum) ->
//                UserBadges.builder()
//                        .id(rs.getLong("id"))
//                        .clerkId(rs.getString("clerk_id"))
//                        .BadgeId(rs.getInt("badge_id"))
//                        .dateEarned(Utils.convertToLocalDateTime(rs.getTimestamp("date_earned")))
//                        .build());
//    }

    public List<UserChallenges> getUserChallenges(String clerkId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(QueryParam.FIRST.getName(), clerkId);
        return jdbcTemplate.query(Queries.getAllUserChallenges, params, (rs, rowNum) ->
                UserChallenges.builder()
                        .id(rs.getLong("id"))
                        .clerkId(rs.getString("clerk_id"))
                        .challengeId(rs.getInt("challenge_id"))
                        .dateStarted(Utils.convertToLocalDateTime(rs.getTimestamp("date_started")))
                        .dateEnded(Utils.convertToLocalDateTime(rs.getTimestamp("date_ended")))
                        .status(UserChallengeStatus.getChallengeStatusByName(rs.getString("status")))
                        .build());
    }

    public int[] addBadgesToUser(String clerkId, List<Badge> badgesToAdd) {
        List<SqlParameterSource> parameters = new ArrayList<>();

        for (Badge badge : badgesToAdd) {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue(QueryParam.FIRST.getName(), clerkId);
            params.addValue(QueryParam.SECOND.getName(), badge.id());
            parameters.add(params);
        }

        badgesToAdd.forEach(System.out::println);
        return jdbcTemplate.batchUpdate(Queries.saveUserBadges, parameters.toArray(new SqlParameterSource[0]));
    }

    public long saveSingleUserActivity(String clerkId, ActivityAction action, Integer actionId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(QueryParam.FIRST.getName(), clerkId);
        params.addValue(QueryParam.SECOND.getName(), action.getName());
        params.addValue(QueryParam.THIRD.getName(), actionId);
        return jdbcTemplate.update(Queries.saveUserActivity, params);
    }

    public int[] saveManyUserActivity(List<UserActivityDTO> userActivity) {
        List<SqlParameterSource> parameters = new ArrayList<>();

        for (UserActivityDTO dto : userActivity) {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue(QueryParam.FIRST.getName(), dto.clerkId());
            params.addValue(QueryParam.SECOND.getName(), dto.action().getName());
            params.addValue(QueryParam.THIRD.getName(), dto.actionId());
            parameters.add(params);
        }

        return jdbcTemplate.batchUpdate(Queries.saveUserActivity, parameters.toArray(new SqlParameterSource[0]));
    }

    public int[] saveManyUserPoints(List<UserPointsDTO> userPoints) {
        List<SqlParameterSource> parameters = new ArrayList<>();

        for (UserPointsDTO dto : userPoints) {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue(QueryParam.FIRST.getName(), dto.clerkId());
            params.addValue(QueryParam.SECOND.getName(), dto.points());
            params.addValue(QueryParam.THIRD.getName(), dto.type().getName());
            params.addValue(QueryParam.FOURTH.getName(), dto.tier().getName());
            params.addValue(QueryParam.FIFTH.getName(), dto.elementId());
            parameters.add(params);
        }

        return jdbcTemplate.batchUpdate(Queries.saveUserPoints, parameters.toArray(new SqlParameterSource[0]));
    }



}
