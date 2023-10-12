package com.coderiders.gamificationservice.utilities;

import lombok.Getter;

@Getter
public enum QueryParam {
    FIRST("first"),
    SECOND("second"),
    THIRD("third");


    private final String name;

    QueryParam(String name) {
        this.name = name;
    }
}
