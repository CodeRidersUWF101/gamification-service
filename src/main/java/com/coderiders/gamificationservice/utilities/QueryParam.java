package com.coderiders.gamificationservice.utilities;

import lombok.Getter;

@Getter
public enum QueryParam {
    FIRST("first"),
    SECOND("second"),
    THIRD("third"),
    FOURTH("fourth"),
    FIFTH("fifth");

    private final String name;

    QueryParam(String name) {
        this.name = name;
    }
}
