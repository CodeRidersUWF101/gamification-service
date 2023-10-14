package com.coderiders.gamificationservice.models.enums;

import lombok.Getter;

@Getter
public enum UserChallengeStatus {

    STARTED("STARTED_CHALLENGE"),
    COMPLETED("COMPLETED_CHALLENGE"),
    FAILED("FAILED_CHALLENGE"),
    ABANDONED("ABANDONED_CHALLENGE");

    private final String name;

    UserChallengeStatus(String name) {
        this.name = name;
    }

    public static UserChallengeStatus getChallengeStatusByName(String name) {
        return switch (name) {
          case "STARTED_CHALLENGE" -> STARTED;
          case "COMPLETED_CHALLENGE" -> COMPLETED;
          case "FAILED_CHALLENGE" -> FAILED;
          case "ABANDONED_CHALLENGE" -> ABANDONED;
          default -> throw new IllegalArgumentException("Unknown UserChallengeStatus: " + name);
        };
    }
}
