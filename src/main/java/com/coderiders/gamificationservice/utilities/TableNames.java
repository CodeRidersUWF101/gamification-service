package com.coderiders.gamificationservice.utilities;

import lombok.Getter;

@Getter
public enum TableNames {
    LEADERBOARD("leaderboards"),

    BADGES("badges"),
    POINTS_SYSTEM("pointssystem"),
    CHALLENGES("readingchallenges"),

    USER_NOTIFICATIONS("usernotifications"),
    USER_POINTS("userpoints"),
    USER_CHALLENGES("userchallenges"),
    USER_BADGES("userbadges"),

    READING_LOGS("readinglogs"),
    USER_ACTIVITY_LOG("useractivitylog");

    private final String name;

    TableNames(String name) {
        this.name = name;
    }

}
