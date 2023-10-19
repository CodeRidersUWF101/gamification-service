package com.coderiders.gamificationservice.repository;

import com.coderiders.commonutils.models.ReadingChallenges;
import com.coderiders.commonutils.models.enums.BadgeType;
import com.coderiders.commonutils.models.enums.ChallengeFrequency;
import com.coderiders.gamificationservice.models.db.Badges;
import com.coderiders.gamificationservice.models.db.PointsSystem;
import com.coderiders.gamificationservice.models.dto.TiersThresholdsDTO;
import com.coderiders.gamificationservice.models.enums.ElementType;
import com.coderiders.gamificationservice.utilities.AdminQueries;
import com.coderiders.gamificationservice.utilities.QueryParam;
import com.coderiders.gamificationservice.utilities.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.util.Arrays;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class AdminRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<Badges> getAllBadges() {
        return jdbcTemplate.query(AdminQueries.ALL_BADGES, badgesMapper());
    }

    public List<Badges> getAllBadgesByType(BadgeType type) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(QueryParam.FIRST.getName(), type.getName());
        return jdbcTemplate.query(AdminQueries.BADGES_BY_TYPE, params, badgesMapper());
    }

    public List<Badges> getAllBadgesByTier(short tier) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(QueryParam.FIRST.getName(), tier);
        return jdbcTemplate.query(AdminQueries.BADGES_BY_TIER, params, badgesMapper());
    }

    public Badges getBadgeById(long id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(QueryParam.FIRST.getName(), id);
        return jdbcTemplate.queryForObject(AdminQueries.BADGES_BY_ID, params, badgesMapper());
    }

    public List<ReadingChallenges> getAllChallenges() {
        return jdbcTemplate.query(AdminQueries.ALL_CHALLENGES, challengesRowMapper());
    }

    public List<ReadingChallenges> getAllChallengesByTime(boolean isLimitedTime) {
        String sql = isLimitedTime ? AdminQueries.LIMITED_TIME_CHALLENGES : AdminQueries.PERMANENT_CHALLENGES;
        return jdbcTemplate.query(sql, challengesRowMapper());
    }

    public List<PointsSystem> getEntirePointsSystem() {
        return jdbcTemplate.query(AdminQueries.ALL_POINTS, (rs, rowNum) ->
                PointsSystem.builder()
                .id(rs.getLong("id"))
                .elementType(ElementType.getElementTypeByName(rs.getString("element_type")))
                .tier(rs.getShort("tier"))
                .pointsAwarded(rs.getInt("points_awarded"))
                .build());
    }

    public List<TiersThresholdsDTO> getAllTiersAndThresholds() {
        return jdbcTemplate.query(AdminQueries.ALL_TIERS_AND_THRESHOLDS, (rs, rowNum) -> {
            Array sqlArray = rs.getArray("all_thresholds");
            Integer[] thresholds = (Integer[])sqlArray.getArray();
            return new TiersThresholdsDTO(
                    BadgeType.getBadgeTypeByName(rs.getString("badge_type")),
                    rs.getInt("lowest_tier"),
                    rs.getInt("lowest_threshold"),
                    rs.getInt("highest_tier"),
                    rs.getInt("highest_threshold"),
                    Arrays.stream(thresholds).mapToInt(Integer::intValue).toArray()
            );
        });
    }



    private RowMapper<ReadingChallenges> challengesRowMapper() {
        return (rs, rowNum) -> ReadingChallenges.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .frequency(ChallengeFrequency.getChallengeTypeByName(rs.getString("frequency")))
                .type(BadgeType.getBadgeTypeByName(rs.getString("type")))
                .threshold(rs.getInt("threshold"))
                .duration(rs.getInt("duration"))
                .startDate(Utils.convertToLocalDateTime(rs.getTimestamp("start_date")))
                .endDate(Utils.convertToLocalDateTime(rs.getTimestamp("end_date")))
                .pointsAwarded(rs.getInt("points_awarded"))
                .build();
    }

    private RowMapper<Badges> badgesMapper() {
        return (rs, rowNum) -> Badges.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .type(BadgeType.getBadgeTypeByName(rs.getString("type")))
                .threshold(rs.getInt("threshold"))
                .tier(rs.getShort("tier"))
                .imageUrl(rs.getString("image_url"))
                .build();
    }
}
