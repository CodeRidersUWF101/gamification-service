package com.coderiders.gamificationservice.models.commonutils.models.enums;
import lombok.Getter;

@Getter
public enum ChallengeFrequency {

    DAILY("Daily", 7),
    WEEKLY("Weekly", 7),
    MONTHLY("Monthly", 30),
    YEARLY("Yearly", 365);

    private final String name;
    private final int lookAhead;

    ChallengeFrequency(String name, int lookAhead) {
        this.name = name;
        this.lookAhead = lookAhead;
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

    public static int getChallengeLookAhead(String name) {
        return switch (name) {
            case "Daily" -> ChallengeFrequency.DAILY.lookAhead;
            case "Weekly" -> ChallengeFrequency.WEEKLY.lookAhead;
            case "Monthly" -> ChallengeFrequency.MONTHLY.lookAhead;
            case "Yearly" -> ChallengeFrequency.YEARLY.lookAhead;
            default -> throw new IllegalArgumentException("Unknown ChallengeType: " + name);
        };
    }
}

