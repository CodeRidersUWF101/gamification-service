package com.coderiders.gamificationservice.repository;

import com.coderiders.gamificationservice.models.db.Badges;
import com.coderiders.gamificationservice.models.db.PointsSystem;
import com.coderiders.gamificationservice.models.db.ReadingChallenges;
import com.coderiders.gamificationservice.models.enums.BadgeType;
import com.coderiders.gamificationservice.models.enums.ChallengeType;
import com.coderiders.gamificationservice.models.enums.ElementType;
import com.coderiders.gamificationservice.models.enums.Tiers;
import com.coderiders.gamificationservice.utilities.AdminQueries;
import com.coderiders.gamificationservice.utilities.QueryParam;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

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

    public List<Badges> getAllBadgesByTier(Tiers tier) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(QueryParam.FIRST.getName(), tier.getName());
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
        return jdbcTemplate.query(AdminQueries.ALL_POINTS, pointsSystemRowMapper());
    }

    private RowMapper<PointsSystem> pointsSystemRowMapper() {
        return ((rs, rowNum) -> PointsSystem.builder()
                .id(rs.getLong("id"))
                .elementType(ElementType.getElementTypeByName(rs.getString("element_type")))
                .tierLevel(Tiers.getTiersByName(rs.getString("tier_level")))
                .pointsAwarded(rs.getInt("points_awarded"))
                .build());
    }

    private RowMapper<ReadingChallenges> challengesRowMapper() {
        return (rs, rowNum) -> ReadingChallenges.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .type(ChallengeType.getChallengeTypeByName(rs.getString("type")))
                .startDate(rs.getTimestamp("start_date"))
                .endDate(rs.getTimestamp("end_date"))
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
                .tier(Tiers.getTiersByName(rs.getString("tier")))
                .imageUrl(rs.getString("image_url"))
                .build();
    }
}
