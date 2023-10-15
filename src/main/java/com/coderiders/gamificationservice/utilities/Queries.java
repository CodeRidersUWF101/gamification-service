package com.coderiders.gamificationservice.utilities;

public class Queries {

    public static final String getUserReadingLogs = selectAllFromTableByFieldOrderAsc(TableNames.READING_LOGS, TableField.CLERK_ID, TableField.DATE);
    public static final String getUserStatistics = selectAllFromFunction(TableField.USER_STATS);
    public static final String getAllUserBadges = selectAllFromTableByClerkId(TableNames.USER_BADGES, TableField.CLERK_ID);
    public static final String getAllUserChallenges = selectAllFromTableByClerkId(TableNames.USER_CHALLENGES, TableField.CLERK_ID);
    public static final String getUserBadgeExpended = """
            SELECT
                b.*,
                ps.points_awarded
            FROM UserBadges ub
            JOIN Badges b ON b.id = ub.badge_id
            JOIN PointsSystem ps ON ps.element_type = 'Badge' AND ps.tier = b.tier
            WHERE clerk_id = :first
            """;

    public static final String getConsecutiveDaysReading = """
            WITH ConsecutiveLogs AS (
                SELECT DISTINCT DATE(date) AS distinct_date
                FROM ReadingLogs
                WHERE clerk_id = :first AND DATE(date) >= :second -- inclusive
                ORDER BY DATE(date) DESC
            ),
            ConsecutiveDays AS (
                SELECT distinct_date, LEAD(distinct_date) OVER (ORDER BY distinct_date DESC) AS next_date
                FROM ConsecutiveLogs
            )
            SELECT COUNT(*)
            FROM ConsecutiveDays
            WHERE next_date IS NULL OR distinct_date = next_date + INTERVAL '1 day';
            """;

    public static final String saveUserPoints = """
            INSERT INTO userpoints (clerk_id, points, element_type, tier, element_id)
            VALUES
                (:first, :second, :third, :fourth, :fifth)
            """;

    public static final String getUserChallengesExpanded = """
            SELECT
            c.id,
            uc.id as user_challenge_id,
            c.name,
            c.description,
            c.frequency,
            c.type,
            c.threshold,
            c.start_date as challengeStartDate,
            c.end_date as challengeEndDate,
            c.points_awarded,
            uc.status,
            uc.date_started as UserChallengeStartDate
            FROM userchallenges uc
            JOIN readingchallenges c ON uc.challenge_id = c.id
            WHERE clerk_id = :first AND uc.status = 'STARTED_CHALLENGE';
            """;

    public static final String savePages = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (:%s, :%s, :%s, :%s)",
                TableNames.READING_LOGS.getName(),
                TableField.CLERK_ID.getName(), TableField.PAGES_READ.getName(), TableField.BOOK_ID.getName(), TableField.ACTION.getName(),
                QueryParam.FIRST.getName(),  QueryParam.SECOND.getName(), QueryParam.THIRD.getName(), QueryParam.FOURTH.getName());

    public static final String getUserPoints = String.format("SELECT %s FROM %s WHERE %s = :%s",
                TableField.TOTAL_POINTS.getName(), TableNames.USER_TOTAL_POINTS.getName(),
                TableField.CLERK_ID.getName(), QueryParam.FIRST.getName());

    public static final String saveUserActivity = String.format("INSERT INTO %s (%s, %s, %s) VALUES (:%s, :%s, :%s)",
                TableNames.USER_ACTIVITY_LOG.getName(),
            TableField.CLERK_ID.getName(), TableField.ACTION.getName(), TableField.ACTION_ID.getName(),
            QueryParam.FIRST.getName(), QueryParam.SECOND.getName(), QueryParam.THIRD.getName());

    public static final String saveUserBadges = insertByTwoFields(TableNames.USER_BADGES, TableField.CLERK_ID, TableField.BADGE_ID);
    public static final String updateUserChallenges = """
            UPDATE UserChallenges
            SET
                status = :first,
                date_ended = :second
            WHERE
                clerk_id = :third
            AND
                id = :fourth
            """;

    /*****************************************************************************************************************/
    private static String selectAllFromTableByFieldOrderAsc(TableNames table, TableField field, TableField orderByField) {
        return String.format("SELECT * FROM %s WHERE %s = :%s ORDER BY %s ASC",
                table.getName(), field.getName(), QueryParam.FIRST.getName(), orderByField.getName());
    }
    private static String selectAllFromFunction(TableField functionName) {
        return String.format("SELECT * FROM %s(:%s)", functionName.getName(), QueryParam.FIRST.getName());
    }

    private static String selectAllFromTableByClerkId(TableNames table, TableField field) {
        return String.format("SELECT * FROM %s WHERE %s = :%s", table.getName(), field.getName(), QueryParam.FIRST.getName());
    }

    private static String insertByTwoFields(TableNames table, TableField field1, TableField field2) {
        return String.format("INSERT INTO %s (%s, %s) VALUES (:%s, :%s)",
                table.getName(), field1.getName(), field2.getName(),
                QueryParam.FIRST.getName(), QueryParam.SECOND.getName());
    }

}
