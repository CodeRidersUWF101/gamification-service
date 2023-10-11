package com.coderiders.gamificationservice.models.enums;

import lombok.Getter;

@Getter
public enum ElementType {

    BADGE("Badge"),
    CHALLENGE("Challenge");

    private final String name;

    ElementType(String name) {
        this.name = name;
    }

    public static ElementType getElementTypeByName(String name) {
        return switch (name) {
            case "Badge" -> BADGE;
            case "Challenge" -> CHALLENGE;
            default -> throw new IllegalArgumentException("Unknown ElementType: " + name);
        };
    }

}
