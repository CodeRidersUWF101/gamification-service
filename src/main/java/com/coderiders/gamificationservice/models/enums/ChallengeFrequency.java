package com.coderiders.gamificationservice.models.enums;

import lombok.Getter;

@Getter
public enum ChallengeFrequency {

    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly");

    private final String name;

    ChallengeFrequency(String name) {
        this.name = name;
    }

    public static ChallengeFrequency getChallengeTypeByName(String name) {
        return switch (name) {
            case "Daily" -> DAILY;
            case "Weekly" -> WEEKLY;
            case "Monthly" -> MONTHLY;
            case "Yearly" -> YEARLY;
            default -> throw new IllegalArgumentException("Unknown ChallengeType: " + name);
        };
    }
}
