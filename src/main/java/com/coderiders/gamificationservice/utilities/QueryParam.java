package com.coderiders.gamificationservice.utilities;

import lombok.Getter;

@Getter
public enum QueryParam {
    TYPE("type"),
    TIER("qTier"),
    NULL("NULL"),
    ID("qID");

    private final String name;

    QueryParam(String name) {
        this.name = name;
    }
}
