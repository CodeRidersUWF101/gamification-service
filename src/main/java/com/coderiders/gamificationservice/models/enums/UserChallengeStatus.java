package com.coderiders.gamificationservice.models.enums;

import lombok.Getter;

@Getter
public enum UserChallengeStatus {

    STARTED_CHALLENGE("STARTED_CHALLENGE"),
    COMPLETED_CHALLENGE("COMPLETED_CHALLENGE"),
    FAILED_CHALLENGE("FAILED_CHALLENGE"),
    ABANDONED_CHALLENGE("ABANDONED_CHALLENGE");

    private final String name;

    UserChallengeStatus(String name) {
        this.name = name;
    }

    public static UserChallengeStatus getChallengeStatusByName(String name) {
        return switch (name) {
          case "STARTED_CHALLENGE" -> STARTED_CHALLENGE;
          case "COMPLETED_CHALLENGE" -> COMPLETED_CHALLENGE;
          case "FAILED_CHALLENGE" -> FAILED_CHALLENGE;
          case "ABANDONED_CHALLENGE" -> ABANDONED_CHALLENGE;
          default -> throw new IllegalArgumentException("Unknown UserChallengeStatus: " + name);
        };
    }
}
