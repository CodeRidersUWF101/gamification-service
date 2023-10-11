package com.coderiders.gamificationservice.models.enums;

import lombok.Getter;

@Getter
public enum UserChallengeStatus {

    STARTED("STARTED"),
    COMPLETED("COMPLETED"),
    FAILED("FAILED"),
    ABANDONED("ABANDONED");

    private final String name;

    UserChallengeStatus(String name) {
        this.name = name;
    }

    public UserChallengeStatus getChallengeStatusByName(String name) {
        return switch (name) {
          case "STARTED" -> STARTED;
          case "COMPLETED" -> COMPLETED;
          case "FAILED" -> FAILED;
          case "ABANDONED" -> ABANDONED;
          default -> throw new IllegalArgumentException("Unknown UserChallengeStatus: " + name);
        };
    }
}
