package com.coderiders.gamificationservice.models.enums;

import lombok.Getter;

@Getter
public enum BadgeType {

    PAGES("Pages"),
    FRIENDS("Friends"),
    STREAK("Streak"),
    BOOKS("Books"),
    ENGAGEMENT("Engagement"),
    COLLECTOR("Collector"),
    COMPLETION("Completion"),
    CHALLENGES("Challenges");

    private final String name;

    BadgeType(String name) {
        this.name = name;
    }

    public static BadgeType getBadgeTypeByName(String name) {
        return switch (name) {
            case "Pages" -> PAGES;
            case "Friends" -> FRIENDS;
            case "Streak" -> STREAK;
            case "Books" -> BOOKS;
            case "Engagement" -> ENGAGEMENT;
            case "Collector" -> COLLECTOR;
            case "Completion" -> COMPLETION;
            case "Challenges" -> CHALLENGES;
            default -> throw new IllegalArgumentException("Unknown BadgeType: " + name);
        };
    }


}
