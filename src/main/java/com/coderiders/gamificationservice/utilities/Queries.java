package com.coderiders.gamificationservice.utilities;

public class Queries {

    public static final String getUserReadingLogs = selectAllFromTableByClerkIdOrderAsc(TableNames.READING_LOGS, TableField.CLERK_ID, QueryParam.FIRST, TableField.DATE);
    public static final String getUserStatistics = selectAllFromFunction(TableField.USER_STATS, QueryParam.FIRST);
    public static final String getAllUserBadges = selectAllFromTableByClerkId(TableNames.USER_BADGES, TableField.CLERK_ID, QueryParam.FIRST);
    public static final String getAllUserChallenges = selectAllFromTableByClerkId(TableNames.USER_CHALLENGES, TableField.CLERK_ID, QueryParam.FIRST);

    public static String savePages() {
        return String.format("INSERT INTO %s (%s, %s, %s) VALUES (:%s, :%s, :%s)",
                TableNames.READING_LOGS.getName(),
                TableField.CLERK_ID.getName(), TableField.PAGES_READ.getName(), TableField.BOOK_ID.getName(),
                QueryParam.FIRST.getName(),  QueryParam.SECOND.getName(), QueryParam.THIRD.getName());
    }
    public static String getUserPoints() {
        return String.format("SELECT %s FROM %s WHERE %s = :%s",
                TableField.TOTAL_POINTS.getName(), TableNames.USER_TOTAL_POINTS.getName(),
                TableField.CLERK_ID.getName(), QueryParam.FIRST.getName());
    }

    /*****************************************************************************************************************/
    private static String selectAllFromTableByClerkIdOrderAsc(TableNames table, TableField field, QueryParam param, TableField orderByField) {
        return String.format("SELECT * FROM %s WHERE %s = :%s ORDER BY %s ASC",
                table.getName(), field.getName(),param.getName(), orderByField.getName());
    }
    private static String selectAllFromFunction(TableField functionName, QueryParam param) {
        return String.format("SELECT * FROM %s(:%s)", functionName.getName(), param.getName());
    }

    private static String selectAllFromTableByClerkId(TableNames table, TableField field, QueryParam param) {
        return String.format("SELECT * FROM %s WHERE %s = :%s", table.getName(), field.getName(), param.getName());
    }

}
