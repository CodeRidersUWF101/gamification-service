//package com.coderiders.gamificationservice.models.enums;
//
//import lombok.Getter;
//
//@Getter
//@Deprecated
//public enum Tiers {
//    @Deprecated
//    TIER_1("Tier1", 1),
//    @Deprecated
//    TIER_2("Tier2", 2),
//    @Deprecated
//    TIER_3("Tier3", 3),
//    @Deprecated
//    TIER_4("Tier4",  4),
//    @Deprecated
//    TIER_5("Tier5", 5);
//
//    private final String name;
//    private final int value;
//
//    Tiers(String name, int value) {
//        this.name = name;
//        this.value = value;
//    }
//
//    public static Tiers getTiersByName(String name) {
//        return switch (name) {
//            case "Tier1" -> TIER_1;
//            case "Tier2" -> TIER_2;
//            case "Tier3" -> TIER_3;
//            case "Tier4" -> TIER_4;
//            case "Tier5" -> TIER_5;
//            default -> throw new IllegalArgumentException("Unknown Tier: " + name);
//        };
//    }
//
//    public static Tiers getTiersBValue(int value) {
//        return switch (value) {
//            case 1 -> TIER_1;
//            case 2 -> TIER_2;
//            case 3 -> TIER_3;
//            case 4 -> TIER_4;
//            case 5 -> TIER_5;
//            default -> throw new IllegalArgumentException("Unknown Tier: " + value);
//        };
//    }
//}
//
