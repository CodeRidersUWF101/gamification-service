package com.coderiders.gamificationservice.models.enums;

import lombok.Getter;

@Getter
public enum BadgeThresholds {
    PAGES(200, 20_000),
    FRIENDS(1, 20),
    STREAK(7, 90),
    BOOKS_READ(5, 75),
    BOOKS_COLLECTED(10, 100),
    BADGES(1, 20),
    CHALLENGES(1, 20);

    private final int min;
    private final int max;

    BadgeThresholds(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public boolean isBetweenThreshold(int value) {
        return min <= value && value <= max;
    }

}
