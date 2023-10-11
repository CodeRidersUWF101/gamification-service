package com.coderiders.gamificationservice.utilities;

import lombok.Getter;

@Getter
public enum TableField {
    TYPE("type"),
    CLERK_ID("clerk_id"),
    ID("id"),
    START_DATE("start_date"),
    TIER("tier");

    private final String name;

    TableField(String name) {
        this.name = name;
    }
}
