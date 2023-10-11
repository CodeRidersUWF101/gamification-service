package com.coderiders.gamificationservice.models.enums;

import lombok.Getter;

@Getter
public enum ChallengeType {

    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly");

    private final String name;

    ChallengeType(String name) {
        this.name = name;
    }

    public static ChallengeType getChallengeTypeByName(String name) {
        return switch (name) {
            case "Daily" -> DAILY;
            case "Weekly" -> WEEKLY;
            case "Monthly" -> MONTHLY;
            case "Yearly" -> YEARLY;
            default -> throw new IllegalArgumentException("Unknown ChallengeType: " + name);
        };
    }
}
