package com.coderiders.gamificationservice.models.commonutils.models.enums;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum ActivityAction {
    STARTED_BOOK("STARTED_BOOK"),
    COMPLETED_BOOK("COMPLETED_BOOK"),
    ADDED_BOOK("ADDED_BOOK"),
    DELETED_BOOK("DELETED_BOOK"),
    STARTED_CHALLENGE("STARTED_CHALLENGE"),
    COMPLETED_CHALLENGE("COMPLETED_CHALLENGE"),
    FAILED_CHALLENGE("FAILED_CHALLENGE"),
    ABANDONED_CHALLENGE("ABANDONED_CHALLENGE"),
    EARNED_BADGE("EARNED_BADGE"),
    EARNED_POINTS("EARNED_POINTS"),
    READ("READ_PAGES");

    private final String name;

    ActivityAction(String name) {
        this.name = name;
    }

    public static ActivityAction getActivityActionByName(String name) {
        return switch (name) {
            case "STARTED_BOOK" -> STARTED_BOOK;
            case "COMPLETED_BOOK" -> COMPLETED_BOOK;
            case "ADDED_BOOK" -> ADDED_BOOK;
            case "DELETED_BOOK" -> DELETED_BOOK;
            case "STARTED_CHALLENGE" -> STARTED_CHALLENGE;
            case "COMPLETED_CHALLENGE" -> COMPLETED_CHALLENGE;
            case "FAILED_CHALLENGE" -> FAILED_CHALLENGE;
            case "ABANDONED_CHALLENGE" -> ABANDONED_CHALLENGE;
            case "EARNED_BADGE" -> EARNED_BADGE;
            case "EARNED_POINTS" -> EARNED_POINTS;
            case "READ_PAGES" -> READ;
            default -> throw new IllegalArgumentException("Unknown ActivityAction: " + name);
        };
    }

    public static List<String> allActivityActions() {
        return Arrays.stream(ActivityAction.values())
                .map(ActivityAction::getName)
                .toList();
    }

    public static boolean isValidActionName(String name) {
        return Arrays.stream(ActivityAction.values())
                .anyMatch(action -> action.getName().equals(name));
    }

}
