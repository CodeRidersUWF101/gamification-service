package com.coderiders.gamificationservice.repository;

import com.coderiders.gamificationservice.models.UserStatistics;
import com.coderiders.gamificationservice.models.db.ReadingLogs;
import com.coderiders.gamificationservice.models.db.UserBadges;
import com.coderiders.gamificationservice.models.db.UserChallenges;
import com.coderiders.gamificationservice.models.enums.UserChallengeStatus;
import com.coderiders.gamificationservice.models.requests.SavePages;
import com.coderiders.gamificationservice.utilities.Queries;
import com.coderiders.gamificationservice.utilities.QueryParam;
import com.coderiders.gamificationservice.utilities.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;


    public long saveReadingLog(SavePages pages) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(QueryParam.FIRST.getName(), pages.clerkId());
        params.addValue(QueryParam.SECOND.getName(), pages.pagesRead());
        params.addValue(QueryParam.THIRD.getName(), pages.bookId());
        return jdbcTemplate.update(Queries.savePages(), params);
    }

    public Integer getUserPoints(String clerkId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(QueryParam.FIRST.getName(), clerkId);
        return jdbcTemplate.queryForObject(Queries.getUserPoints(), params, Integer.class);
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

    public List<UserBadges> getUserBadges(String clerkId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(QueryParam.FIRST.getName(), clerkId);
        return jdbcTemplate.query(Queries.getAllUserBadges, params, (rs, rowNum) ->
                UserBadges.builder()
                        .id(rs.getLong("id"))
                        .clerkId(rs.getString("clerk_id"))
                        .BadgeId(rs.getInt("badge_id"))
                        .dateEarned(Utils.convertToLocalDateTime(rs.getTimestamp("date_earned")))
                        .build());
    }

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
}
