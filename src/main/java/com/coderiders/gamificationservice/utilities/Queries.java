package com.coderiders.gamificationservice.utilities;

public class Queries {
    public static final String ALL_BADGES = getAllFromTable(TableNames.BADGES);
    public static final String BADGES_BY_TYPE = getAllFromTableSingleParam(TableNames.BADGES, TableField.TYPE, QueryParam.TYPE);
    public static final String BADGES_BY_TIER = getAllFromTableSingleParam(TableNames.BADGES, TableField.TIER, QueryParam.TIER);
    public static final String BADGES_BY_ID = getAllFromTableSingleParam(TableNames.BADGES, TableField.ID, QueryParam.ID);

    public static final String ALL_CHALLENGES = getAllFromTable(TableNames.CHALLENGES);
    public static final String CHALLENGES_BY_TYPE = getAllFromTableSingleParam(TableNames.CHALLENGES, TableField.TYPE, QueryParam.TYPE);

    public static final String LIMITED_TIME_CHALLENGES = getAllFromTableSingleParamIsNull(TableNames.CHALLENGES, TableField.START_DATE, false);
    public static final String PERMANENT_CHALLENGES = getAllFromTableSingleParamIsNull(TableNames.CHALLENGES, TableField.START_DATE, true);


    public static String getAllFromTable(TableNames table) {
        return String.format("SELECT * FROM %s", table.getName());
    }

    public static String getAllFromTableSingleParam(TableNames table, TableField field, QueryParam param) {
        return String.format("SELECT * FROM %s WHERE %s = :%s", table.getName(), field.getName(), param.getName());
    }

    public static String getAllFromTableSingleParamIsNull(TableNames table, TableField field, boolean isNull) {
        return isNull
                ? String.format("SELECT * FROM %s WHERE %s IS NULL", table.getName(), field.getName())
                : String.format("SELECT * FROM %s WHERE %s IS NOT NULL", table.getName(), field.getName());
    }
}
