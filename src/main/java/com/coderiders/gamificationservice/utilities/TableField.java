package com.coderiders.gamificationservice.utilities;

import lombok.Getter;

@Getter
public enum TableField {
    TYPE("type"),
    CLERK_ID("clerk_id"),
    ID("id"),
    START_DATE("start_date"),
    TIER("tier"),
    DATE("date"),
    USER_STATS("get_user_statistics"),
    USER_TOTAL_POINTS("UserTotalPoints"),
    PAGES_READ("pages_read"),
    BOOK_ID("book_id"),
    TOTAL_POINTS("TotalPoints"),
    ACTION("action"),
    ACTION_ID("actionID"),
    BADGE_ID("badge_id");

    private final String name;

    TableField(String name) {
        this.name = name;
    }
}
